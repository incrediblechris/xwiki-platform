package com.xpn.xwiki.atom.lifeblog;

import java.text.ParseException;
import java.util.Calendar;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.XWikiIntegrationTest;
import com.xpn.xwiki.atom.WSSEHttpHeader;
import com.xpn.xwiki.atom.XWikiHelper;

public class LifeblogContextIntegrationTest extends XWikiIntegrationTest {

  public void testIsAuthenticatedNullHeader() throws LifeblogServiceException, XWikiException, ParseException {
    String header = null;
    
    XWikiHelper xwikiHelper = new XWikiHelper(context);
    LifeblogContext lifeblogContext = new LifeblogContext();
    lifeblogContext.setXWikiHelper(xwikiHelper);

    boolean authenticated = lifeblogContext.isAuthenticated(header);
    
    assertFalse("Not authenticated if no WSSE header", authenticated);
  }

  public void testIsAuthenticated() throws LifeblogServiceException, XWikiException, ParseException {
    String nonce = "d36e316282959a9ed4c89851497a717f";
    String created = WSSEHttpHeader.CalendarToW3CDSTFormat(Calendar.getInstance());
    String login = "Admin";
    String password = "admin";
    String passwordDigest = new String(Base64.encodeBase64(DigestUtils.sha(nonce + created + password)));

    String header = "UsernameToken Username=\"" + login +"\", PasswordDigest=\"" + passwordDigest + "\", Nonce=\""+ nonce + "\", Created=\""+ created + "\"";
    
    XWikiHelper xwikiHelper = new XWikiHelper(context);
    LifeblogContext lifeblogContext = new LifeblogContext();
    lifeblogContext.setXWikiHelper(xwikiHelper);

    boolean authenticated = lifeblogContext.isAuthenticated(header);
    
    assertTrue(authenticated);
  }

  public void testIsAuthenticatedSameNonceTwice() throws LifeblogServiceException, XWikiException, ParseException {
    String nonce = "d36e316282959a9ed4c89851497a717f";
    String created = WSSEHttpHeader.CalendarToW3CDSTFormat(Calendar.getInstance());
    String login = "Admin";
    String password = "admin";
    String passwordDigest = new String(Base64.encodeBase64(DigestUtils.sha(nonce + created + password)));

    String header = "UsernameToken Username=\"" + login +"\", PasswordDigest=\"" + passwordDigest + "\", Nonce=\""+ nonce + "\", Created=\""+ created + "\"";
    
    XWikiHelper xwikiHelper = new XWikiHelper(context);
    LifeblogContext lifeblogContext = new LifeblogContext();
    lifeblogContext.setXWikiHelper(xwikiHelper);

    boolean authenticated = lifeblogContext.isAuthenticated(header);    
    assertTrue(authenticated);
    
    authenticated = lifeblogContext.isAuthenticated(header);   
    assertFalse("Same Nonce twice", authenticated);
  }
}
