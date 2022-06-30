<%!
    /** */
    private static final int  BASELENGTH = 255;

    /** */
    private static final int  LOOKUPLENGTH = 64;

    /** */
    private static final int  TWENTYFOURBITGROUP = 24;

    /** */
    private static final int  EIGHTBIT = 8;

    /** */
    private static final int  SIXTEENBIT = 16;

    /** */
    private static final int  SIXBIT = 6;

    /** */
    private static final int  FOURBYTE = 4;

    /** The sign bit as an int */
    private static final int  SIGN = -128;

    /** The padding character */
    private static final byte PAD = (byte) '=';

    /** The alphabet */
    private static final byte [] BASE64_ALPHABET = new byte[BASELENGTH];

    /** The lookup alphabet */
    private static final byte [] LOOKUP_BASE64_ALPHABET = new byte[LOOKUPLENGTH];

    static {

        for (int i = 0; i < BASELENGTH; i++) {
            BASE64_ALPHABET[i] = -1;
        }
        for (int i = 'Z'; i >= 'A'; i--) {
            BASE64_ALPHABET[i] = (byte) (i - 'A');
        }
        for (int i = 'z'; i >= 'a'; i--) {
            BASE64_ALPHABET[i] = (byte) (i - 'a' + 26);
        }

        for (int i = '9'; i >= '0'; i--) {
            BASE64_ALPHABET[i] = (byte) (i - '0' + 52);
        }

        BASE64_ALPHABET['+']  = 62;
        BASE64_ALPHABET['/']  = 63;

        for (int i = 0; i <= 25; i++) {
            LOOKUP_BASE64_ALPHABET[i] = (byte) ('A' + i);
        }

        for (int i = 26, j = 0; i <= 51; i++, j++) {
            LOOKUP_BASE64_ALPHABET[i] = (byte) ('a' + j);
        }

        for (int i = 52,  j = 0; i <= 61; i++, j++) {
            LOOKUP_BASE64_ALPHABET[i] = (byte) ('0' + j);
        }
        LOOKUP_BASE64_ALPHABET[62] = (byte) '+';
        LOOKUP_BASE64_ALPHABET[63] = (byte) '/';

    }

	public static byte[] decode(byte[] base64Data) {
        // Should we throw away anything not in base64Data ?

        // handle the edge case, so we don't have to worry about it later
        if (base64Data.length == 0) {
            return new byte[0];
        }

        int      numberQuadruple    = base64Data.length / FOURBYTE;
        byte     decodedData[]      = null;
        byte     b1 = 0, b2 = 0, b3 = 0, b4 = 0, marker0 = 0, marker1 = 0;

        int encodedIndex = 0;
        int dataIndex    = 0;
        {
            // this block sizes the output array properly - rlw
            int lastData = base64Data.length;
            // ignore the '=' padding
            while (base64Data[lastData - 1] == PAD) {
                if (--lastData == 0) { return new byte[0]; }
            }
            decodedData = new byte[lastData - numberQuadruple];
        }

        for (int i = 0; i < numberQuadruple; i++) {
            dataIndex = i * 4;
            marker0   = base64Data[dataIndex + 2];
            marker1   = base64Data[dataIndex + 3];

            b1 = BASE64_ALPHABET[base64Data[dataIndex]];
            b2 = BASE64_ALPHABET[base64Data[dataIndex + 1]];

            if (marker0 != PAD && marker1 != PAD) {     //No PAD e.g 3cQl
                b3 = BASE64_ALPHABET[marker0];
                b4 = BASE64_ALPHABET[marker1];

                decodedData[encodedIndex]   = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex + 1] = (byte) (((b2 & 0xf) << 4)
                    | ((b3 >> 2) & 0xf));
                decodedData[encodedIndex + 2] = (byte) (b3 << 6 | b4);
            } else if (marker0 == PAD) {    //Two PAD e.g. 3c[Pad][Pad]
                decodedData[encodedIndex]   = (byte) (b1 << 2 | b2 >> 4) ;
            } else if (marker1 == PAD) {    //One PAD e.g. 3cQ[Pad]
                b3 = BASE64_ALPHABET[marker0];
                decodedData[encodedIndex]   = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex + 1] = (byte) (((b2 & 0xf) << 4) 
                    | ((b3 >> 2) & 0xf));
            }
            encodedIndex += 3;
        }
        return decodedData;
    }
	static boolean inArray(String[] pArray,String pCode)
	{
		if ((pArray!=null)&&(pCode!=null))
		{
			for (int lPtr=0;lPtr<pArray.length;lPtr++)
				if(pArray[lPtr].equalsIgnoreCase(pCode)) return true;
		}
		return false;
	}
	static String getUser(String pAuthHead)
	{
		if(pAuthHead!=null)
		{
			String lUserPass=new String(decode(pAuthHead.substring(6).getBytes()));
			String lUser=lUserPass.substring(0,lUserPass.indexOf(":"));
			return lUser;
		}
		return null;
	}
%>