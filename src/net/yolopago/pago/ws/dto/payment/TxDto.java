package net.yolopago.pago.ws.dto.payment;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class TxDto implements Parcelable {
	private Long id;
	private String paymentStatus;
	private String transactionType;
	private String paymentType;

	private Date capturedDate;
	private Date payedDate;
	private Double amount;
	private Double amountReturns;
	private Date day;
	private Double totalAmount;
	private Double totalAmountReturns;

	private MerchantDto merchantDto;
	private TerminalDto terminalDto;
	private SellerDto sellerDto;
	private VoucherDto voucherDto;
	private TxDto parentDto;
	private Set<TxDto> paymentDtoChilds = new HashSet<TxDto>();
	private Boolean hasChilds;
	private Date outputProsa;

	private String cardHolder;
	private String cardBrand="";
	private String issuingBank="";
	private String cardType="";
	private String maskedPAN;
	private String voucher_file;


	protected TxDto(Parcel in) {
		if (in.readByte() == 0) {
			id = null;
		} else {
			id = in.readLong();
		}
		paymentStatus = in.readString();
		transactionType = in.readString();
		paymentType = in.readString();
		if (in.readByte() == 0) {
			amount = null;
		} else {
			amount = in.readDouble();
		}
		if (in.readByte() == 0) {
			amountReturns = null;
		} else {
			amountReturns = in.readDouble();
		}
		if (in.readByte() == 0) {
			totalAmount = null;
		} else {
			totalAmount = in.readDouble();
		}
		if (in.readByte() == 0) {
			totalAmountReturns = null;
		} else {
			totalAmountReturns = in.readDouble();
		}
		parentDto = in.readParcelable(TxDto.class.getClassLoader());
		byte tmpHasChilds = in.readByte();
		hasChilds = tmpHasChilds == 0 ? null : tmpHasChilds == 1;
		cardHolder = in.readString();
		cardBrand = in.readString();
		issuingBank = in.readString();
		cardType = in.readString();
		maskedPAN = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (id == null) {
			dest.writeByte((byte) 0);
		} else {
			dest.writeByte((byte) 1);
			dest.writeLong(id);
		}
		dest.writeString(paymentStatus);
		dest.writeString(transactionType);
		dest.writeString(paymentType);
		if (amount == null) {
			dest.writeByte((byte) 0);
		} else {
			dest.writeByte((byte) 1);
			dest.writeDouble(amount);
		}
		if (amountReturns == null) {
			dest.writeByte((byte) 0);
		} else {
			dest.writeByte((byte) 1);
			dest.writeDouble(amountReturns);
		}
		if (totalAmount == null) {
			dest.writeByte((byte) 0);
		} else {
			dest.writeByte((byte) 1);
			dest.writeDouble(totalAmount);
		}
		if (totalAmountReturns == null) {
			dest.writeByte((byte) 0);
		} else {
			dest.writeByte((byte) 1);
			dest.writeDouble(totalAmountReturns);
		}
		dest.writeParcelable(parentDto, flags);
		dest.writeByte((byte) (hasChilds == null ? 0 : hasChilds ? 1 : 2));
		dest.writeString(cardHolder);
		dest.writeString(cardBrand);
		dest.writeString(issuingBank);
		dest.writeString(cardType);
		dest.writeString(maskedPAN);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<TxDto> CREATOR = new Creator<TxDto>() {
		@Override
		public TxDto createFromParcel(Parcel in) {
			return new TxDto(in);
		}

		@Override
		public TxDto[] newArray(int size) {
			return new TxDto[size];
		}
	};

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public Date getCapturedDate() {
		return capturedDate;
	}

	public void setCapturedDate(Date capturedDate) {
		this.capturedDate = capturedDate;
	}

	public Date getPayedDate() {
		return payedDate;
	}

	public void setPayedDate(Date payedDate) {
		this.payedDate = payedDate;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getAmountReturns() {
		return amountReturns;
	}

	public void setAmountReturns(Double amountReturns) {
		this.amountReturns = amountReturns;
	}

	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Double getTotalAmountReturns() {
		return totalAmountReturns;
	}

	public void setTotalAmountReturns(Double totalAmountReturns) {
		this.totalAmountReturns = totalAmountReturns;
	}

	public MerchantDto getMerchantDto() {
		return merchantDto;
	}

	public void setMerchantDto(MerchantDto merchantDto) {
		this.merchantDto = merchantDto;
	}

	public TerminalDto getTerminalDto() {
		return terminalDto;
	}

	public void setTerminalDto(TerminalDto terminalDto) {
		this.terminalDto = terminalDto;
	}

	public SellerDto getSellerDto() {
		return sellerDto;
	}

	public void setSellerDto(SellerDto sellerDto) {
		this.sellerDto = sellerDto;
	}

	public VoucherDto getVoucherDto() {
		return voucherDto;
	}

	public void setVoucherDto(VoucherDto voucherDto) {
		this.voucherDto = voucherDto;
	}

	public TxDto getParentDto() {
		return parentDto;
	}

	public void setParentDto(TxDto parentDto) {
		this.parentDto = parentDto;
	}

	public Set<TxDto> getPaymentDtoChilds() {
		return paymentDtoChilds;
	}

	public void setPaymentDtoChilds(Set<TxDto> paymentDtoChilds) {
		this.paymentDtoChilds = paymentDtoChilds;
	}

	public Boolean getHasChilds() {
		return hasChilds;
	}

	public void setHasChilds(Boolean hasChilds) {
		this.hasChilds = hasChilds;
	}

	public Date getOutputProsa() {
		return outputProsa;
	}

	public void setOutputProsa(Date outputProsa) {
		this.outputProsa = outputProsa;
	}

	public String getCardHolder() {
		return cardHolder;
	}

	public void setCardHolder(String cardHolder) {
		this.cardHolder = cardHolder;
	}

	public String getCardBrand() {
		return cardBrand;
	}

	public void setCardBrand(String cardBrand) {
		this.cardBrand = cardBrand;
	}

	public String getIssuingBank() {
		if(issuingBank==null){
			return "";
		}
		return issuingBank;
	}

	public void setIssuingBank(String issuingBank) {
		this.issuingBank = issuingBank;
	}

	public String getCardType() {
		if(cardType==null){
			return "";
		}
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getMaskedPAN() {
		return maskedPAN;
	}

	public void setMaskedPAN(String maskedPAN) {
		this.maskedPAN = maskedPAN;
	}

	public String getVoucherFile() {	return voucher_file;	}

	public void setVoucherFile(String voucher_file) {	this.voucher_file = voucher_file;	}

	public static DiffUtil.ItemCallback<TxDto> DIFF_CALLBACK = new DiffUtil.ItemCallback<TxDto>() {
		@Override
		public boolean areItemsTheSame(@NonNull TxDto oldItem, @NonNull TxDto newItem) {
			return oldItem.id.equals(newItem.id);
		}

		@Override
		public boolean areContentsTheSame(@NonNull TxDto oldItem, @NonNull TxDto newItem) {
			return oldItem.equals(newItem);
		}
	};

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (this.getClass() != obj.getClass())
			return false;

		if (obj == this)
			return true;

		TxDto txDto = (TxDto) obj;

		return txDto.id.equals(this.id);
	}
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}