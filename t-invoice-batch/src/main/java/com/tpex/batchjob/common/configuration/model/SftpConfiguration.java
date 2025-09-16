package com.tpex.batchjob.common.configuration.model;

import lombok.Data;

/**
 * Instantiates a new sftp configuration.
 */
@Data
public class SftpConfiguration {
	
	/** The host. */
	private String host;
	
	/** The port. */
	private int port;
	
	/** The user. */
	private String user;
	
	/** The password. */
	private String password;
	
	/** The private key. */
	private String privateKey;
	
	/** The private key passphrase. */
	private String privateKeyPassphrase;
	
	/** The directory. */
	private String directory;
}
