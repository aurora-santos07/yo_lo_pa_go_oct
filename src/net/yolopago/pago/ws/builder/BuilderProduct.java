package net.yolopago.pago.ws.builder;

import net.yolopago.pago.db.entity.Price;
import net.yolopago.pago.db.entity.Product;
import net.yolopago.pago.db.entity.ProductTax;
import net.yolopago.pago.db.entity.Tax;
import net.yolopago.pago.ws.dto.product.PriceDto;
import net.yolopago.pago.ws.dto.product.ProductDto;
import net.yolopago.pago.ws.dto.product.ProductTaxDto;
import net.yolopago.pago.ws.dto.product.TaxDto;

public class BuilderProduct {

	public Product build(ProductDto productDto) {
		Product product = new Product();

		product.set_id(productDto.getId());
		product.setName(productDto.getName());
		product.setDescription(productDto.getDescription());
		product.setSku(productDto.getSku());
		product.setProductType(productDto.getProductTypeDto().getName());
//		product.getPhotos().addAll(productDto.getPhotos());
		product.setIdPrice(productDto.getPriceDto().getId());
		return product;
	}

	public Price build(PriceDto priceDto) {
		Price price = new Price();

		price.set_id(priceDto.getId());
		price.setIdProduct(priceDto.getProductDto().getId());
		price.setPriceStatus(priceDto.getPriceStatus());
		price.setFixedPrice(priceDto.getFixedPrice());
		price.setMaxPrice(priceDto.getMaxPrice());
		price.setBuyPrice(priceDto.getBuyPrice());
		price.setSellPrice(priceDto.getSellPrice());
		price.setEffectiveDate(priceDto.getEffectiveDate().getTime());

		return price;
	}

	public ProductTax build(ProductTaxDto productTaxDto) {
		ProductTax productTax = new ProductTax();

		productTax.set_id(productTaxDto.getId());
		productTax.setIdProduct(productTaxDto.getProductDto().getId());
		productTax.setIdTax(productTaxDto.getTaxDto().getId());
		productTax.setRate(productTaxDto.getRate());

		return productTax;
	}

	public Tax build(TaxDto taxDto) {
		Tax tax = new Tax();

		tax.set_id(taxDto.getId());
		tax.setName(taxDto.getName());

		return tax;
	}
}
