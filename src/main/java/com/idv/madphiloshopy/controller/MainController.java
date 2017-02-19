package com.idv.madphiloshopy.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Textbox;

import com.idv.madphiloshopy.service.LoginService;

public class MainController extends SelectorComposer<Component> {
	
	
	@WireVariable
	private LoginService loginService;
	@Wire
	private Textbox account;
	@Wire
	private Textbox password;
	
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
       
    }
    
    @Listen("onClick = #login")
    public void onLogin(){
    	
    	loginService.LoginDefinit(account.getValue(), password.getValue(),this.getPage());
    	
    }
    

    
}
