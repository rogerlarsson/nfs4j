package org.dcache.xdr;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;

import com.sun.grizzly.Context;
import com.sun.grizzly.util.OutputWriter;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RpcCall implements XdrAble {

    private final static Logger _log = Logger.getLogger(RpcCall.class.getName());
    
    /**
     * Supported RPC protocol version
     */
    private final static int RPCVERS = 2;

    /**
     * RPC program number
     */
    private int _prog;
    
    /**
     * RPC program version number
     */
    private int _version;
    
    /**
     * RPC program procedure number
     */
    private int _proc;
    
    /**
     *  RPC protocol version number
     */
    private int _rpcvers;
    
    private RpcAuth _authVerf;
    private RpcAuth _auth;
    
    /**
     * Grizzly information context.
     * @see com.sun.grizzly.Context
     */
    private final Context _context;

    private int _xid;
    
    public RpcCall(int xid, Context context) {
        _context = context;
        _xid = xid;
    }

    public void xdrDecode(Xdr xdr) throws XdrException {
        _rpcvers = xdr.get_int();
        _prog = xdr.get_int();
        _version = xdr.get_int();
        if( _rpcvers != RPCVERS ) {
            throw new RpcException("RPC version mismatch: " + _rpcvers,
                    new RpcMismatchReply(2, 2));
        }
        _proc = xdr.get_int();
        int authType = xdr.get_int();
        switch(authType) {
            case RpcAuthType.UNIX :
                _auth = new RpcAuthTypeUnix();
                break;
            case RpcAuthType.NONE:
                _auth = new RpcAuthTypeNone();
        }
        xdr.decode(_auth);

        authType = xdr.get_int();
        switch(authType) {
            case RpcAuthType.UNIX :
                _authVerf = new RpcAuthTypeUnix();
                break;
            case RpcAuthType.NONE:
                _authVerf = new RpcAuthTypeNone();
        }
        xdr.decode(_authVerf);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(_auth);

        sb.append("RPC vers.: ").append(_rpcvers).append("\n");
        sb.append("Program  : ").append(_prog).append("\n");
        sb.append("Version  : ").append(_version).append("\n");
        sb.append("Procedure: ").append(_proc).append("\n");

        return sb.toString();
    }

    @Override
    public void xdrEncode(Xdr xdr) throws XdrException {
        // TODO Auto-generated method stub        
    }

    
    /**
     * Get RPC call program number.
     * 
     * @return version number
     */
    public int getProgram() {
        return _prog;
    }

    /**
     * @return the RPC call program version
     */
    public int getProgramVersion() {
        return _version;
    }

    /**
     * @return the RPC call program procedure
     */
    public int getProcedure() {
        return _proc;
    }

    public RpcAuth getAuth() {
        return _auth;
    }
   
    public RpcAuth getAuthVerf() {
        return _authVerf;
    }
    
    
    public void reply(RpcReply reply) {
        Xdr xdr = new Xdr(1024);

        try {
            xdr.startEncode();
            xdr.put_int(_xid);
            xdr.put_int(RpcMessageType.REPLY);
            xdr.encode(getAuthVerf());
            xdr.encode(reply);
            xdr.stopEncode();

            SelectableChannel channel = _context.getSelectionKey().channel();
            ByteBuffer message = xdr.body();

            OutputWriter.flushChannel(channel, message);
            
        } catch (XdrException e) {
            _log.log(Level.WARNING, "Xdr exception: ", e);
        } catch (IOException e) {
            _log.log(Level.SEVERE, "Failed send reply: ", e);
        }
    }
    
}
