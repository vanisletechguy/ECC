package com.ecc.action;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import com.ecc.entity.Point;

public class Ecc {
	public static void main(String[] args) {
		//initial elliptic curve configuration
		//equation: y^2 = x*3 + ax + b -> y^2 = x^3 -4x + 4
		
		double a = -4, b = 4;
		
		//base point on the curve
		Point P = new Point();
		P.setPointX(BigDecimal.valueOf(-2));
		P.setPointY(BigDecimal.valueOf(-2));
		
		//key exchange - elliptic curve diffie hellman
		BigInteger ka = new BigInteger("100000000021"); //Alice's private key
		Point alicePublic = applyDoubleAndAddMethod(P, ka, a, b);
		
		System.out.println("Alice's public key:");
		displayPoint(alicePublic);
		
		BigInteger kb = new BigInteger("100000000077"); //Bob's private key
		Point bobPublic = applyDoubleAndAddMethod(P, kb, a, b);
		
		System.out.println("Bob's public key:");
		displayPoint(bobPublic);
		
		//------------------------------------------
		System.out.println("shared key: ");
		Point aliceShared = applyDoubleAndAddMethod(bobPublic, ka, a, b);
		Point bobShared = applyDoubleAndAddMethod(alicePublic, kb, a, b);
		
		System.out.println("Alice produces this shared key: ");
		displayPoint(aliceShared);
		System.out.println("Bob produces this shared key: ");
		displayPoint(bobShared);
	}
	
	public static Point applyDoubleAndAddMethod(Point P, BigInteger k, double a, double b) {
		Point tempPoint = new Point();
		tempPoint.setPointX(P.getPointX());
		tempPoint.setPointY(P.getPointY());
		
		String kAsBinary = k.toString(2); //convert to binary
		//System.out.println("("+k+")10 = ("+kAsBinary+")2");
		
		for(int i=1; i<kAsBinary.length(); i++) {
			int currentBit = Integer.parseInt(kAsBinary.substring(i, i+1));
			tempPoint = pointAddition(tempPoint, tempPoint, a, b);
			if(currentBit == 1) {
				tempPoint = pointAddition(tempPoint, P, a, b);
			}
		}
		return tempPoint;
	}
	
	public static void displayPoint(Point P) {		
		int scale = 50; //precision
		System.out.println("("+P.getPointX().setScale(scale, BigDecimal.ROUND_HALF_UP)+", "+P.getPointY().setScale(scale, BigDecimal.ROUND_HALF_UP)+")");
	}
	
	public static Point pointAddition(Point P, Point Q, double a, double b) {
		MathContext mc = new MathContext(128);
		BigDecimal x1 = P.getPointX();
		BigDecimal y1 = P.getPointY();
		BigDecimal x2 = Q.getPointX();
		BigDecimal y2 = Q.getPointY();
		BigDecimal beta;
		
		//if P == Q 
		if(x1.compareTo(x2) == 0 && y1.compareTo(y2) == 0) {
			//apply doubling
			beta = (BigDecimal.valueOf(3).multiply(x1.multiply(x1))
					.add(BigDecimal.valueOf(a)))
					.divide((BigDecimal.valueOf(2).multiply(y1)), mc);	
		} else {
			//apply point addition
			beta = (y2.subtract(y1))
					.divide((x2.subtract(x1)),mc) ;
		}
		BigDecimal x3 = (beta.multiply(beta)).subtract(x1).subtract(x2);
		BigDecimal y3 = (beta.multiply(x1.subtract(x3))).subtract(y1);
		
		Point R = new Point();
		R.setPointX(x3);;
		R.setPointY(y3);;
		
		return R;
	}
}
