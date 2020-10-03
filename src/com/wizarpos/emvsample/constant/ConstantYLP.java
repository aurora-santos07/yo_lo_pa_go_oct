package com.wizarpos.emvsample.constant;

public interface ConstantYLP
{

	final String APP_TAG = "yolopago";


	final byte STATE_NEED_SING               =15;

	final byte EMV_PIN_BYPASS_CONFIRM       = 7;  // notify Application confirm to Accepted PIN Bypass or not

	final int RECIBO_DECLINADO =0;
	final int RECIBO_ACEPTADO  =1;
	final int RECIBO_REVEconRSADO =2;
	final int RECIBO_PRODUCTOS =3;
	final int RECIBO_RECHAZADO =4;
	final int RECIBO_TIMEOUT   =5;
	final int RECIBO_CONEXION =6;
	final int RECIBO_ERROR_APP=7;
	final int RECIBO_REVERSADO=8;
	final int RECIBO_CANCELADO=9;

	//ASANTOS nombre de parámetros recibido desde apps externas
	String EXTRA_CONCEPT = "CONCEPT";
	String EXTRA_AMOUNT = "AMOUNT";

}
