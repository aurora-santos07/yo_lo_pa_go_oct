package net.yolopago.pago.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wizarpos.emvsample.MainApp;
import com.wizarpos.emvsample.R;
import com.wizarpos.emvsample.activity.Product;
import com.wizarpos.emvsample.activity.cancelacionPrint;
import com.wizarpos.emvsample.databinding.ProductDetailBinding;
import com.wizarpos.emvsample.databinding.TxDetailBinding;
import com.wizarpos.emvsample.printer.PrinterException;

import net.yolopago.pago.activity.MainActivity;
import net.yolopago.pago.recyclerview.TxDetailAdapter;
import net.yolopago.pago.repository.PaymentRepository;
import net.yolopago.pago.utilities.DecimalDigitsInputFilter;
import net.yolopago.pago.viewmodel.MerchantViewModel;
import net.yolopago.pago.viewmodel.PaymentViewModel;
import net.yolopago.pago.viewmodel.TicketViewModel;
import net.yolopago.pago.ws.dto.payment.TxDto;
import net.yolopago.pago.ws.dto.payment.UserDto;
import net.yolopago.pago.ws.dto.ticket.TicketLineDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DetailProductFragment extends AbstractFragment implements View.OnClickListener {
	private static final String TAG = "DetailProductFragment";
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,##0.00",new DecimalFormatSymbols(Locale.US));
	private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("$###,###,##0.00",new DecimalFormatSymbols(Locale.US));
	private ProductDetailBinding binding;
	private Product product;
	private int indexProduct;


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		binding = DataBindingUtil.inflate(inflater, R.layout.product_detail, container, false);
		return binding.getRoot();
	}


	@SuppressLint("RestrictedApi")
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		binding.concepto.setText(product.producto);
		binding.cantidad.setText(product.cantidad);
		binding.precioUnitario.setText(DECIMAL_FORMAT.format(product.getPrecio()));
		binding.precioUnitario.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(7,2)});
		binding.totalProd.setText(MONEY_FORMAT.format(product.getTotal()));



		binding.btnGuardarProducto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveProductos();
			}
		});


		binding.upCount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cantidadAdd(1);
			}
		});

		binding.downCount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cantidadAdd(-1);
			}
		});


		TextView.OnEditorActionListener listener=new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				    v.clearFocus();
					updateTotal();
					return true;
				}
				return false;
			}
		};

		binding.cantidad.setOnEditorActionListener(listener);
		binding.precioUnitario.setOnEditorActionListener(listener);

		binding.cancelarProducto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteProduct();
			}
		});
		binding.back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				presentFragmentResume();
			}
		});

	}

	private void deleteProduct() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
		//alertDialogBuilder.setTitle("¡Quitar producto!");
		alertDialogBuilder
				.setMessage("¿Desea quitar el producto del carrito compra?")
				.setCancelable(false)
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						((MainActivity)getActivity()).getProducList().remove(indexProduct);
						((MainActivity)getActivity()).setBadgeCount();
						//Toast.makeText(getContext(), "¡Se quito el producto de la compra!", Toast.LENGTH_LONG).show();
						presentFragmentResume();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).create().show();
	}

	private void cantidadAdd(int number) {

		int maxDigit = 6;
		Integer cantidadInt = binding.cantidad.getText().toString().equals("")?0:Integer.parseInt(binding.cantidad.getText().toString());
		String cantidad = "";
		if (binding.cantidad.getText().toString().length() <= maxDigit && cantidadInt >= 0 ) {
			cantidadInt = cantidadInt +number;
			cantidadInt = (cantidadInt<0)?0:(cantidadInt>=Math.pow(10,maxDigit))?(int) (Math.pow(10,maxDigit)-1):cantidadInt;
			if(cantidadInt!=0) {
				cantidad = String.format("%d", cantidadInt);
			}else if(cantidadInt==0 && number==-1){

				deleteProduct();
				cantidad="1";
			}
			binding.cantidad.setText(cantidad);

		}
	}

	private void updateTotal() {
		Double precioUnitario=0d;
		int cantidad=0;
		try {
			precioUnitario=Double.parseDouble(binding.precioUnitario.getText().toString());
			cantidad=Integer.parseInt(binding.cantidad.getText().toString());
		}catch (NumberFormatException e){
		}
		binding.totalProd.setText(MONEY_FORMAT.format(precioUnitario*cantidad));

	}

	public void saveProductos(){
			if (binding.concepto.getText().toString().equals("") ||  binding.precioUnitario.getText().toString().equals("")) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
				// Configura el titulo.
				//alertDialogBuilder.setTitle("Error Producto");
				alertDialogBuilder
						.setMessage("Ingresa la información del producto")
						.setCancelable(false)
						.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})
						.create().show();
			} else if(binding.precioUnitario.getText().toString().equals(".") || binding.precioUnitario.getText().toString().equals("0.00") ||
					binding.precioUnitario.getText().toString().equals("0.0") || binding.precioUnitario.getText().toString().equals("0")){
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
				// Configura el titulo.
				//alertDialogBuilder.setTitle("Error Producto");
				// Configura el mensaje.
				alertDialogBuilder
						.setMessage("Ingresa el precio para el producto")
						.setCancelable(false)
						.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})
						.create().show();
			}else if(binding.cantidad.getText().toString().equals("") || binding.cantidad.getText().toString().equals("0") || binding.cantidad.getText().toString().equals("00")
					|| binding.cantidad.getText().toString().equals("000") || binding.cantidad.getText().toString().equals("0000")
					|| binding.cantidad.getText().toString().equals("00000") ){
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
				// Configura el titulo.
				//alertDialogBuilder.setTitle("Error Producto");
				// Configura el mensaje.
				alertDialogBuilder
						.setMessage("Ingresa la cantidad para el producto")
						.setCancelable(false)
						.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})
						.create().show();
			}else{
				Double precioUnitario = 0d;
				Double cantidad = 0d;
				
				try {
					NumberFormat format = NumberFormat.getInstance(Locale.US);
					Number numberParse = null;
					try {
						numberParse = format.parse(binding.precioUnitario.getText().toString());
					} catch (ParseException e) {
						numberParse=0;
					}
					precioUnitario=numberParse.doubleValue();
					cantidad=Double.parseDouble(binding.cantidad.getText().toString());
				}catch (NumberFormatException e){}
				        Product product = new Product(binding.concepto.getText().toString(), binding.cantidad.getText().toString(), precioUnitario, precioUnitario * cantidad, precioUnitario * cantidad);

					if(totalAmount(product,indexProduct)<10000000) {
						product.setCantidad(binding.cantidad.getText().toString());
						product.setProducto(binding.concepto.getText().toString());
						product.setPrecio(precioUnitario);
						product.setTotal(precioUnitario * cantidad);
						product.setSum(product.getTotal());
						((MainActivity) getActivity()).getProducList().set(indexProduct, product);
						((MainActivity) getActivity()).setBadgeCount();
						//Toast.makeText(getContext(), "Producto Guardado.",
						//		Toast.LENGTH_SHORT).show();
						presentFragmentResume();
					}else {
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
						alertDialogBuilder
								.setMessage("El monto total no puede ser mayor o igual a 10M")
								.setCancelable(false)
								.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
									}
								})
								.create().show();

					}
			}

	}

	@Override
	public void onClick(View v) {

	}
	private void presentFragmentResume(){
		FragmentResumen fragmentResumen = new FragmentResumen();
		setFragment(fragmentResumen);
	}

	public void setProduct(Product product, int i) {
		this.product=product;
		this.indexProduct=i;
	}
	public Double totalAmount(Product product,int indexProduct) {
		Double total=0d;
		ArrayList<Product> productos=((MainActivity) getActivity()).getProducList();
		if(productos!=null) {
			for (int i = 0; i < productos.size(); i++) {
				Double data3 = i==indexProduct?product.getTotal():productos.get(i).getTotal();
				total +=(data3);
			}
		}
		return total;
	}
}
