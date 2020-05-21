package com.boray.EncryPwd;

public class TEA_Encrypt {
	/********************************************************************* 
	*                           tea���� 
	*����:v:Ҫ���ܵ�����,����Ϊ8�ֽ� 
	*     k:�����õ�key,����Ϊ16�ֽ� 
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
	*                           tea���� 
	*����:v:Ҫ���ܵ�����,����Ϊ8�ֽ� 
	*     k:�����õ�key,����Ϊ16�ֽ� 
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
	*                           �����㷨 
	*����:src:Դ����,��ռ�ռ����Ϊ8�ֽڵı���.������ɺ�����Ҳ������� 
	*     size_src:Դ���ݴ�С,��λ�ֽ� 
	*     key:��Կ,16�ֽ� 
	*����:���ĵ��ֽ��� 
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
	    //�����Ĳ���Ϊ8�ֽڵı���  
		/*int n = 8 - size_src % 8;//��temp��λ������8�ı���,��Ҫ����λ��
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
	    //����  
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
	*                           �����㷨 
	*����:src:Դ����,��ռ�ռ����Ϊ8�ֽڵı���.������ɺ�����Ҳ������� 
	*     size_src:Դ���ݴ�С,��λ�ֽ� 
	*     key:��Կ,16�ֽ� 
	*����:���ĵ��ֽ���,���ʧ��,����0 
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
			
	    //�жϳ����Ƿ�Ϊ8�ı���  
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
	    //����  
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
