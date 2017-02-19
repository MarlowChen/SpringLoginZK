package com.idv.madphiloshopy.service.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zkplus.spring.SpringUtil;
import com.idv.madphiloshopy.service.LoginService;

@Service("loginService")
public class LoginServicImpl implements LoginService,Initiator{
	@WireVariable
	JdbcTemplate templates;
    
	
	public boolean LoginDefinit(String account, String password ,Page page) {
		
        Selectors.wireVariables(page, this, Selectors.newVariableResolvers(getClass(), null));

		String md5str;
		boolean LoginDefinit = false;
		String sql = "select * from userdata WHERE account=?";
		List<Map<String, Object>> alls = templates.queryForList(sql, new Object[] { account });

		if (alls.size() != 0) {
			for (int i = 0; i < alls.size(); i++) {

				alls.get(i);

				md5str = PasswordToMD5(password);

				if (md5str.equals(alls.get(i).get("password").toString())) {
					LoginDefinit = true;
					break;

				} else {
					LoginDefinit = false;
				}

			}
		}
		return LoginDefinit;
	}

	
	public String PasswordToMD5(String password) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(password.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}


	public void doInit(Page page, Map<String, Object> args) throws Exception {
		// TODO Auto-generated method stub
		
	}


}
