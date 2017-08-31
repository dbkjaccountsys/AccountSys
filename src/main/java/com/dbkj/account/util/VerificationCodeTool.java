package com.dbkj.account.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

public class VerificationCodeTool {

	private static Random random=new Random();
	
	/**
	 * 产生加减乘除的随机字符串和运算结果
	 * @return
	 */
	public static RandomResult randomString(){
		int xx=random.nextInt(10);
		int yy=random.nextInt(10);
		
		StringBuilder randomString=new StringBuilder();
		//产生随机数0 1 2
		int randomOperands=(int) Math.round(Math.random()*2);
		int result=0;
		switch(randomOperands){
		case 0:
			result=xx*yy;
			randomString.append(xx);
			randomString.append("×");
			randomString.append(yy);
			break;
		case 1:
			if(!(xx==0)&&yy%xx==0){
				result=yy/xx;
				randomString.append(yy);
				randomString.append("÷");
				randomString.append(xx);
			}else{
				result=yy+xx;
				randomString.append(yy);
				randomString.append("+");
				randomString.append(xx);
			}
			break;
		case 2:
			result=yy-xx;
			randomString.append(yy);
			randomString.append("-");
			randomString.append(xx);
			break;
		default:
			result=yy+xx;
			randomString.append(yy);
			randomString.append("+");
			randomString.append(xx);
			break;
		}
		randomString.append("=");
		return new RandomResult(randomString.toString(), result);
	}
	
	
	
	/**
	 * 封装产生随机字符串和运算结构
	 * @author DELL
	 *
	 */
	public static class RandomResult{
		
		public RandomResult(String randomString,int result){
			this.randomString=randomString;
			this.result=result;
		}
		
		private String randomString;
		private int result;
		public String getRandomString() {
			return randomString;
		}
		public void setRandomString(String randomString) {
			this.randomString = randomString;
		}
		public int getResult() {
			return result;
		}
		public void setResult(int result) {
			this.result = result;
		}
		
	}
	
	public static void outputImage(int w,int h,File outputFile,String code){
		if(outputFile==null){
			return;
		}
		File dir=outputFile.getParentFile();
		if(!dir.exists()){
			dir.mkdirs();
		}
		FileOutputStream fos=null;
		try {
			outputFile.createNewFile();
			fos=new FileOutputStream(outputFile);
			outputImage(w, h, fos, code);
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void outputImage(int w,int h,OutputStream os,String code){
		int verifySize=code.length();
		BufferedImage image=new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2=image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color[] colors=new Color[5];
		Color[] colorSpace=new Color[]{
				Color.WHITE,Color.CYAN,Color.GRAY,Color.LIGHT_GRAY,Color.MAGENTA,
				Color.ORANGE,Color.PINK,Color.YELLOW
		};
		float[] fractions=new float[colors.length];
		for(int i=0;i<colors.length;i++){
			colors[i]=colorSpace[random.nextInt(colorSpace.length)];
			fractions[i]=random.nextFloat();
		}
		Arrays.sort(fractions);
		
		g2.setColor(Color.GRAY);
		g2.fillRect(0, 0, w, h);
		
		Color c=getRandColor(200, 500);
		g2.setColor(c);
		g2.fillRect(0, 2, w, h-4);
		
		g2.setColor(getRandColor(160, 200));
		for(int i=0;i<20;i++){
			int x=random.nextInt(w-1);
			int y=random.nextInt(h-1);
			int xl=random.nextInt(6)+1;
			int yl=random.nextInt(12)+1;
			g2.drawLine(x, y, x+xl+40, y+yl+20);
		}
		
		float yawpRate=0.05f;
		int area = (int) (yawpRate * w * h);  
        for (int i = 0; i < area; i++) {  
            int x = random.nextInt(w);  
            int y = random.nextInt(h);  
            int rgb = getRandomIntColor();  
            image.setRGB(x, y, rgb);  
        }  
        
        shear(g2, w, h, c);
        
        g2.setColor(getRandColor(100, 160));  
        int fontSize = h-4;  
        Font font = new Font("Algerian", Font.ITALIC, fontSize);  
        g2.setFont(font);  
        char[] chars = code.toCharArray();  
        for(int i = 0; i < verifySize; i++){  
            AffineTransform affine = new AffineTransform();  
            int degree = new Random().nextInt() % 15;  
            //Positive point of view  
            if(i<verifySize-1){
            	 affine.rotate(degree * Math.PI / 180, 5+i*25, 20);  
                 //affine.setToRotation(Math.PI / 4 * random.nextDouble() * (random.nextBoolean() ? 1 : -1), (w / verifySize) * i + fontSize/2, h/2);  
                 g2.setTransform(affine);  
            }
//          g2.drawChars(chars, i, 1, ((w-10) / verifySize) * i + 5, h/2 + fontSize/2 - 10);  
            g2.drawChars(chars, i, 1, ((w-10) / verifySize) * i + 5, h/2 + fontSize/2-2);  
        }  
          
        g2.dispose();  
        try {
			ImageIO.write(image, "jpg", os);
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
	 private static Color getRandColor(int fc, int bc) {  
	    if (fc > 255)  
	         fc = 255;  
	    if (bc > 255)  
	         bc = 255;  
	    int r = fc + random.nextInt(bc - fc);  
	    int g = fc + random.nextInt(bc - fc);  
	    int b = fc + random.nextInt(bc - fc);  
	    return new Color(r, g, b);  
	 }  
	      
	 private static int getRandomIntColor() {  
	    int[] rgb = getRandomRgb();  
	    int color = 0;  
	    for (int c : rgb) {  
	        color = color << 8;  
	        color = color | c;  
	    }  
	    return color;  
	}  
	      
	private static int[] getRandomRgb() {  
	   int[] rgb = new int[3];  
	   for (int i = 0; i < 3; i++) {  
	       rgb[i] = random.nextInt(255);  
	   }  
	   return rgb;  
	}  
	  
	private static void shear(Graphics g, int w1, int h1, Color color) {  
	   shearX(g, w1, h1, color);  
	   shearY(g, w1, h1, color);  
	}  
	      
	private static void shearX(Graphics g, int w1, int h1, Color color) {    
	   int period = random.nextInt(2);  
	  
	   boolean borderGap = true;  
	   int frames = 1;  
	   int phase = random.nextInt(2);  
	  
	   for (int i = 0; i < h1; i++) {  
		   double d = (double) (period >> 1)  
	                    * Math.sin((double) i / (double) period  
	                    + (6.2831853071795862D * (double) phase)  
	                    / (double) frames);  
	       g.copyArea(0, i, w1, 1, (int) d, 0);  
	       if (borderGap) {  
	            g.setColor(color);  
	            g.drawLine((int) d, i, 0, i);  
	            g.drawLine((int) d + w1, i, w1, i);  
	       }  
	   }  
	  
	}  
	  
	private static void shearY(Graphics g, int w1, int h1, Color color) {  
	     int period = random.nextInt(40) + 10; // 50;  
	  
	     boolean borderGap = true;  
	     int frames = 20;  
	     int phase = 7;  
	     for (int i = 0; i < w1; i++) {  
	         double d = (double) (period >> 1)  
	                  	* Math.sin((double) i / (double) period  
	                    + (6.2831853071795862D * (double) phase)  
	                    / (double) frames);  
		     g.copyArea(i, 0, 1, h1, 0, (int) d);  
		     if (borderGap) {  
		          g.setColor(color);  
		          g.drawLine(i, (int) d, i, 0);  
		          g.drawLine(i, (int) d + h1, i, h1);  
		     }  
	     }  
	}  
	
	public static void main(String[] args) {
		RandomResult result=randomString();
		System.out.println("result:"+result.getResult());
		outputImage(65, 30, new File("C:/Users/DELL/Desktop/code.jpg"), result.getRandomString());
	}
}
