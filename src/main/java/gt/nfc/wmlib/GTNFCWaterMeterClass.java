package gt.nfc.wmlib;

import android.nfc.tech.IsoDep;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;


public class GTNFCWaterMeterClass {
    static {
        System.loadLibrary("WMNFCLib");
    }


    public  native int EncryptBuffer(byte[] inputbuffer, byte[] WritingBuf,   int datasize) ;
    public  native byte[]    GetReadingCommand( byte index   ) ;
    Gson gson = new GsonBuilder().setLenient().create();
    SahlMesasage SahlMesasage = new SahlMesasage();
    final WriteMsg ChargeRequest = new WriteMsg() ;

    WriteMsg WriteMsg = new WriteMsg() ;
     public String  GetReading(IsoDep isoDep)
    {

        byte[] buffer = isoDep.getTag().getId() ;
        final String[][] Responsemsg = {{null}};
        Responsemsg[0] = new String[5] ;
        try {
            if(!isoDep.isConnected() )
                isoDep.connect();
        } catch (IOException e) {
            //                         throw new RuntimeException(e);
            return   null ;
        }


        for(byte i = 0 ; i < 5 ; i++)
        {
            //String StReadingCommand = GetReadingCommand(i) ;
            byte[] ResponseBuffer = new byte[0];
            byte[] Decryptedcmdmsg = GetReadingCommand(i) ; //new byte[0];
//            try {
//                Decryptedcmdmsg = AesDecryptor.ConvertfromHextoByteArr( new String(StReadingCommand.getBytes(), "UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                throw new RuntimeException(e);
//            }
            try {
                ResponseBuffer = isoDep.transceive(Decryptedcmdmsg);
            }
            catch (IOException e)  {
                //throw new RuntimeException(e);
                return null ;
            }
            if(i==0 )
            {
                byte[] ConnectRespomse = new byte[ResponseBuffer.length + 2] ;
                System.arraycopy(ResponseBuffer, 0, ConnectRespomse, 0, ResponseBuffer.length - 2 );
                System.arraycopy(buffer, 0, ConnectRespomse,   ResponseBuffer.length - 2  , 4 );
                ResponseBuffer = new byte[ResponseBuffer.length + 2] ;
                System.arraycopy( ConnectRespomse, 0,ResponseBuffer, 0, ConnectRespomse.length   );
            }
            else
            {
                byte[] ConnectRespomse = new byte[ResponseBuffer.length - 2] ;
                System.arraycopy(ResponseBuffer, 0, ConnectRespomse, 0, ResponseBuffer.length - 2 );
                ResponseBuffer = new byte[ConnectRespomse.length ] ;
                System.arraycopy( ConnectRespomse, 0,ResponseBuffer, 0, ConnectRespomse.length   );
            }
            String APDUResponse = SahlMesasage.bytesToHex(ResponseBuffer);
            Responsemsg[0][i] = APDUResponse;//SahlMesasage.EncryptBuffer(ResponseBuffer) ;
        }
        ChargeRequest.indexcmd = WriteMsg.indexcmd ;
        ChargeRequest.cmdmsg = Responsemsg[0] ;

        CustomerChargeMsg _customerChargeMsg = PrepareCustomerChargeMsg( "10" , ChargeRequest , buffer ) ;
        String retval = gson.toJson(_customerChargeMsg   ) ;
        return retval;

    }


    public String SetCharging (IsoDep isoDep , String _SahlChargeReceiptData)
    {
        SahlChargeReceiptData SahlChargeReceiptData = gson.fromJson(_SahlChargeReceiptData , SahlChargeReceiptData.class) ;
        final String[][] Responsemsg = {{null}};
        if (SahlChargeReceiptData != null)
            if (SahlChargeReceiptData.Buffer.cmdmsg.length > 0) {
                Responsemsg[0] = new String[SahlChargeReceiptData.Buffer.cmdmsg.length];
                try {
                    if (!isoDep.isConnected())
                        isoDep.connect();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //Response =
                for (int i = 0; i < SahlChargeReceiptData.Buffer.cmdmsg.length; i++) {
                    String cmdmsg = SahlChargeReceiptData.Buffer.cmdmsg[i];
                    if (cmdmsg != null) {
                        byte[] Decryptedcmdmsg = SahlMesasage.DecryptBuffer(cmdmsg);
                        byte[] ResponseBuffer = new byte[0];
                        try {
                            ResponseBuffer = isoDep.transceive(Decryptedcmdmsg);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }


                        String APDUResponse = SahlMesasage.bytesToHex(ResponseBuffer);
                        Responsemsg[0][i] = APDUResponse;
                    }

                }
                final WriteMsg ChargeRequest = new WriteMsg() ;
                ChargeRequest.indexcmd = SahlChargeReceiptData.Buffer.indexcmd;
                ChargeRequest.cmdmsg = Responsemsg[0];
                return gson.toJson(ChargeRequest);
            }
        return "" ;
    }




    CustomerChargeMsg PrepareCustomerChargeMsg(String ChargeValue  , WriteMsg ChargeMsg  , byte[] buffer)
    {
        CustomerChargeMsg _customerChargeMsg = new CustomerChargeMsg() ;
        SahlMesasage SahlMesasage = new SahlMesasage();
        _customerChargeMsg.ChargeValue = ChargeValue;
        _customerChargeMsg.Response = "Sucess" ;

        ByteBuffer wrapped = ByteBuffer.wrap(buffer); // big-endian by default
        Integer num = wrapped.getInt(); // 1
        ChargeMsg.setCardID(num);
        if(ChargeMsg.cmdmsg.length > 0) {
            String[] cmdmsg = new String[ChargeMsg.cmdmsg.length];
            for (int i = 0; i < ChargeMsg.cmdmsg.length; i++) {
                cmdmsg[i] = ChargeMsg.cmdmsg[i] ;
                if(cmdmsg != null) {
                    cmdmsg[i] = SahlMesasage.EncryptBuffer(cmdmsg[i]) ;

                }

            }
            ChargeMsg.cmdmsg = cmdmsg ;
        }
        _customerChargeMsg.WriteMsg =  gson.toJson(ChargeMsg) ;
        return      _customerChargeMsg ;
    }


}

class CustomerChargeMsg {
    public String Response;
    public String WriteMsg;
    public String ChargeValue;
}


 class WriteMsg {
    private int CardID;

    public int getCardID() {
        return CardID;
    }

    public void setCardID(int _CardID) {
        this.CardID = _CardID;
    }

    public int[] indexcmd = null;
    public String[] cmdmsg = null;


}

