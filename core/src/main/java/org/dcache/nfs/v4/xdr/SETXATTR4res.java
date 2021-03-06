/*
 * Automatically generated by jrpcgen 1.0.7+ on 11/1/19, 1:52 PM
 * jrpcgen is part of the "Remote Tea" ONC/RPC package for Java
 * See http://remotetea.sourceforge.net for details
 *
 * This version of jrpcgen adopted by dCache project
 * See http://www.dCache.ORG for details
 */
package org.dcache.nfs.v4.xdr;
import org.dcache.oncrpc4j.rpc.*;
import org.dcache.oncrpc4j.rpc.net.*;
import org.dcache.oncrpc4j.xdr.*;
import java.io.IOException;

public class SETXATTR4res implements XdrAble {
    public int sxr_status;
    public change_info4 sxr_info;

    public SETXATTR4res() {
    }

    public SETXATTR4res(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(sxr_status);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        sxr_status = xdr.xdrDecodeInt();
    }
}
// End of SETXATTR4res.java
