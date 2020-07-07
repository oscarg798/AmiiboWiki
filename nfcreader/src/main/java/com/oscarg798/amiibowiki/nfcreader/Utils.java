/*
 * Copyright 2020 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.oscarg798.amiibowiki.nfcreader;

import java.nio.ByteBuffer;

class Utils {

    public static final int TAG_FILE_SIZE = 532;
    public static final int PAGE_SIZE = 4;
    public static final int AMIIBO_ID_OFFSET = 0x54;
    public static final int APP_ID_OFFSET = 0xB6;
    public static final int APP_ID_LENGTH = 4;
    public static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static long amiiboIdFromTag(byte[] data) throws Exception {
        if (data.length < TAG_FILE_SIZE)
            throw new Exception("Invalid tag data");

        byte[] amiiboId = new byte[4 * 2];
        System.arraycopy(data, AMIIBO_ID_OFFSET, amiiboId, 0, amiiboId.length);
        return ByteBuffer.wrap(amiiboId).getLong();
    }

    public static String amiiboIdToHex(long amiiboId) {
        return String.format("%016X", amiiboId);
    }

    public static String getTail(long id) throws Exception {
        return String.format("%08x", (int)((id & TAIL_MASK) >> TAIL_BITSHIFT));
    }

    public static String getHead(long id) {
        return String.format("%08x", (int)((id & HEAD_MASK) >> HEAD_BITSHIFT));

    }

    public static String getImageUrl(long amiiboId){
        int head = (int)((amiiboId & HEAD_MASK) >> HEAD_BITSHIFT);
        int tail = (int)((amiiboId & TAIL_MASK) >> TAIL_BITSHIFT);;
        return String.format(AMIIBO_API_IMAGE_URL, head, tail);
    }

    public static int TAIL_BITSHIFT = 4 * 0;
    public static long TAIL_MASK = 0x00000000FFFFFFFFL;
    public static long HEAD_MASK = 0xFFFFFFFF00000000L;
    public static int HEAD_BITSHIFT = 4 * 8;
    public static String AMIIBO_API_IMAGE_URL = "https://raw.githubusercontent.com/N3evin/AmiiboAPI/master/images/icon_%08x-%08x.png";
}
