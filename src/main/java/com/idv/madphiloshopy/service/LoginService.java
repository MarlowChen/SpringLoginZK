package com.idv.madphiloshopy.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.zkoss.zk.ui.Page;

public interface LoginService {
	
	public boolean LoginDefinit(String account, String password,Page page);

	public String PasswordToMD5(String password);

}
