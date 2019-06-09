/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package encryption;

import constants.ServerConfig;
import utils.BitUtil;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class MapleAESOFB {

    private byte iv[];
    private Cipher cipher;
    private final short mapleVersion;

    public MapleAESOFB(byte iv[], short mapleVersion) {
        SecretKeySpec sKeySpec = new SecretKeySpec(ServerConfig.key, "AES");
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            System.err.println("ERROR" + e);
        } catch (InvalidKeyException e) {
            System.err.println("Error initalizing the encryption cipher.  Make sure you're using the Unlimited Strength cryptography jar files.");
        }
        this.iv = iv;
        this.mapleVersion = (short) (((mapleVersion >> 8) & 0xFF) | ((mapleVersion << 8) & 0xFF00));
    }

    public byte[] crypt(byte[] data, byte[] dest, int offset) {
        int remaining = data.length;
        int llength = 0x5B0;
        int start = 0;

        try {
            while (remaining > 0) {
                byte[] myIv = BitUtil.multiplyBytes(this.iv, 4, 4);
                if (remaining < llength) {
                    llength = remaining;
                }
                for (int x = start; x < (start + llength); x++) {
                    if ((x - start) % myIv.length == 0) {
                        byte[] newIv = cipher.doFinal(myIv);
                        System.arraycopy(newIv, 0, myIv, 0, myIv.length);

                    }
                    dest[x + offset] = (byte)(data[x] ^ myIv[(x - start) % myIv.length]);
                }
                start += llength;
                remaining -= llength;
                llength = 0x5B4;
            }
            updateIv();
        } catch (IllegalBlockSizeException | BadPaddingException e) {
        }
        return data;
    }

    private void updateIv() {
        byte[] iv = this.iv;
        byte[] in = ServerConfig.kIvMutate.clone();
        for (int i = 0; i < 4; i++) {
            in[0] += ServerConfig.ivArr[(int) in[1] & 0xFF] - iv[i];
            in[1] = (byte) (in[1] - (in[2] ^ ServerConfig.ivArr[(int) iv[i] & 0xFF]));
            in[2] ^= iv[i] + ServerConfig.ivArr[(int) in[3] & 0xFF];
            in[3] = (byte) (ServerConfig.ivArr[(int) iv[i] & 0xFF] + in[3] - in[0]);

            int d = ((int) in[0]) & 0xFF;
            d |= (in[1] << 8) & 0xFF00;
            d |= (in[2] << 16) & 0xFF0000;
            d |= (in[3] << 24) & 0xFF000000;
            int ret_value = d >>> 0x1d;
            d <<= 3;
            ret_value |= d;

            in[0] = (byte) (ret_value & 0xFF);
            in[1] = (byte) ((ret_value >> 8) & 0xFF);
            in[2] = (byte) ((ret_value >> 16) & 0xFF);
            in[3] = (byte) ((ret_value >> 24) & 0xFF);
        }
        this.iv = in;
    }

    public byte[] getPacketHeader(int length) {
        int iiv = (((iv[3]) & 0xFF) | ((iv[2] << 8) & 0xFF00)) ^ mapleVersion;
        int mlength = (((length << 8) & 0xFF00) | (length >>> 8)) ^ iiv;

        return new byte[]{(byte) ((iiv >>> 8) & 0xFF), (byte) (iiv & 0xFF), (byte) ((mlength >>> 8) & 0xFF), (byte) (mlength & 0xFF)};
    }

    public static int getPacketLength(int packetHeader) {
        int packetLength = ((packetHeader >>> 16) ^ (packetHeader & 0xFFFF));
        packetLength = ((packetLength << 8) & 0xFF00) | ((packetLength >>> 8) & 0xFF);
        return packetLength;
    }

    private boolean checkPacket(byte[] packet) {
        return ((((packet[0] ^ iv[2]) & 0xFF) == ((mapleVersion >> 8) & 0xFF)) && (((packet[1] ^ iv[3]) & 0xFF) == (mapleVersion & 0xFF)));
    }

    public boolean checkPacket(int packetHeader) {
        return checkPacket(new byte[]{(byte) ((packetHeader >> 24) & 0xFF), (byte) ((packetHeader >> 16) & 0xFF)});
    }

    @Override
    public String toString() {
        return "IV: " + Arrays.toString(this.iv);
    }
}
