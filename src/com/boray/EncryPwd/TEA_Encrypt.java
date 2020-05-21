package com.boray.EncryPwd;

public class TEA_Encrypt {
	/********************************************************************* 
	*                           tea加密 
	*参数:v:要加密的数据,长度为8字节 
	*     k:加密用的key,长度为16字节 
	**********************************************************************/  
	void encrypt(long v[],int flag,int k[])   
	{  
		long y = v[flag],z = v[flag+1],sum = 0,i;     
		//System.out.println(flag+"<><>"+y+"//"+z);
		int delta = 0x9e3779b9;                  
		int a = k[0],b = k[1],c = k[2],d = k[3];    
	      
	    for (i = 0;i < 32;i++)   
	    {                          
	        sum += delta;  
	        y += ((z << 4) + a) ^ (z + sum) ^ ((z >> 5) + b);  
	        z += ((y << 4) + c) ^ (y + sum) ^ ((y >> 5) + d);  
	        System.out.print(y+"<><><>"+z);
	    }
	    v[flag] = y;  
	    v[flag+1] = z;  
	}  
	  
	/********************************************************************* 
	*                           tea解密 
	*参数:v:要解密的数据,长度为8字节 
	*     k:解密用的key,长度为16字节 
	**********************************************************************/  
	void decrypt(int v[],int k[])   
	{  
		int y = v[0],z = v[1], sum = 0xC6EF3720,i;   
		int delta = 0x9e3779b9;              
		int a = k[0],b = k[1],c = k[2],d = k[3];  
			sum= delta<<5;   
	    for (i = 0;i < 32;i++)   
	    {                           
	        z -= ((y << 4) + c) ^ (y + sum) ^ ((y >> 5) + d);  
	        y -= ((z << 4) + a) ^ (z + sum) ^ ((z >> 5) + b);  
	        sum -= delta;                       
	    }  
	    v[0] = y;  
	    v[1] = z;  
	}
	  
	/********************************************************************* 
	*                           加密算法 
	*参数:src:源数据,所占空间必须为8字节的倍数.加密完成后密文也存放在这 
	*     size_src:源数据大小,单位字节 
	*     key:密钥,16字节 
	*返回:密文的字节数 
	**********************************************************************/  
	  
	int TEA_Encrypt(long src[],int size_src,int key[])  
	{  
		int a = 0;  
		int i = 0;  
		int num = 0;  
		long[] U32Data = new long[16];
		for (int j = 0; j < U32Data.length; j++) {
			U32Data[j] = 0;
		}
		int residues = 0;
	    //将明文补足为8字节的倍数  
		/*int n = 8 - size_src % 8;//若temp的位数不足8的倍数,需要填充的位数
		byte[] encryptStr = new byte[size_src + n];
		encryptStr[0] = (byte)n;
		System.arraycopy(temp, 0, encryptStr, n, temp.length);*/
	   /* residues = size_src % 8;  
	    if (residues != 0)  
	    {  
	        for (i = 0;i < 8 - residues;i++)  
	        {  
	            src[size_src++] = 0;  
	        }  
	    }  */
	    a = 0;
			
	    for(i=0;i<size_src;i+=4)
	    {
	    	U32Data[a] += src[i]<<24;
	    	U32Data[a] += src[i+1]<<16;
	    	U32Data[a] += src[i+2]<<8;
	    	U32Data[a] += src[i+3];
	    	/*System.out.println(U32Data[a]+"//"+(src[i]<<24)+"//"+(src[i+1]<<16)+"//"+
	    						"//"+(src[i+2]<<8)+"//"+src[i+3]);*/
	    	System.out.println(Long.toHexString(U32Data[a]));
	    	a++;
	    }
	    //加密  
	    num = size_src / 8;  
	    for (i = 0;i < num;i++)  
	    {  
				encrypt(U32Data,i*2,key);  
	    }  
			//size_src+=1;
			//src[0] = residues;
	    a = 0;
	    for(i=0;i<size_src;i+=4)
	    {
	    	src[i] = (U32Data[a]>>24);
	    	src[i+1] = (U32Data[a]>>16);
	    	src[i+2] = (U32Data[a]>>8);
	    	src[i+3] =  U32Data[a];
	    	System.out.println(Long.toHexString(U32Data[a]));
	    	a++;
	    }
	    return size_src;  
	}
	  
	/********************************************************************* 
	*                           解密算法 
	*参数:src:源数据,所占空间必须为8字节的倍数.解密完成后明文也存放在这 
	*     size_src:源数据大小,单位字节 
	*     key:密钥,16字节 
	*返回:明文的字节数,如果失败,返回0 
	**********************************************************************/  
	  
	int TEA_Decrypt(int src[],int size_src,int key[])  
	{  
		int i = 0,a = 0;  
		int num = 0;  
		int[] U32Data = new int[16];
		for (int j = 0; j < U32Data.length; j++) {
			U32Data[j] = 0;
		}
		int residues = 0;
			
	    //判断长度是否为8的倍数  
	    if ((size_src-1) % 8 != 0)  
	    {  
	        return 0;  
	    }  
			residues = src[0];
	    a = 0;
	    for(i=1;i<size_src;i+=4)
	    {
	    	U32Data[a] |= src[i]<<24;
	    	U32Data[a] |= src[i+1]<<16;
	    	U32Data[a] |= src[i+2]<<8;
	    	U32Data[a] |= src[i+3];
	    	a++;
	    }
	    //解密  
	    num = (size_src-1) / 8;  
	    for (i = 0;i < num;i++)  
	    {  
	        decrypt(U32Data,key);  
	    }  
	    a = 0;
			size_src -= 1;
	    for(i=0;i<size_src;i+=4)
	    {
	    	src[i] = U32Data[a]>>24;
	    	src[i+1] = U32Data[a]>>16;
	    	src[i+2] = U32Data[a]>>8;
	    	src[i+3] = U32Data[a];
	    	a++;
	    }
			if(residues!=0)
				size_src -= 8 - residues;
	    return size_src;  
	}

}
