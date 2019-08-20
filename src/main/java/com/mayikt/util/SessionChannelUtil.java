package com.mayikt.util;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SessionChannelUtil {
    Session session;
    ChannelSftp channelSftp;
    ChannelExec channelExec;

    public SessionChannelUtil() {
        this.session = null;
        this.channelSftp = null;
        this.channelExec = null;
    }

    public Session sessionConnect(String username, String host, int port, String password, int timeout) throws JSchException {
        JSch jsch = new JSch();
        if (port <= 0)
            this.session = jsch.getSession(username, host);
        else
            this.session = jsch.getSession(username, host, port);

        this.session.setPassword(password);
        this.session.setConfig("StrictHostKeyChecking", "no");

        this.session.connect();

        return this.session;
    }

    public ChannelSftp channelSftpConnect(Session session, int timeout)
            throws JSchException {
        this.channelSftp = ((ChannelSftp) session.openChannel("sftp"));
        this.channelSftp.connect(timeout);

        return this.channelSftp;
    }

    public ChannelExec channelExecConnect(Session session, String command, int timeout)
            throws JSchException {
        this.channelExec = ((ChannelExec) session.openChannel("exec"));
        this.channelExec.setCommand(command);
        this.channelExec.setInputStream(null);
        this.channelExec.connect(timeout);

        return this.channelExec;
    }

    public void closeSession() throws Exception {
        if (this.channelSftp != null)
            this.channelSftp.disconnect();

        if (this.channelExec != null)
            this.channelExec.disconnect();

        if (this.session != null)
            this.session.disconnect();
    }

    public void closeChannelSftp()
            throws Exception {
        if (this.channelSftp != null)
            this.channelSftp.disconnect();
    }

    public void closeChannelExec()
            throws Exception {
        if (this.channelExec != null)
            this.channelExec.disconnect();
    }
}