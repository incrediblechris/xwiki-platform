package com.xpn.xwiki.atom;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import com.xpn.xwiki.atom.lifeblog.LifeblogServiceException;

public class WSSEHeaderTest extends TestCase {

  public void testParseUserName() {
    Pattern pattern = Pattern.compile(WSSEHttpHeader.getUserNameExpression());
    Matcher matcher = pattern.matcher("Username=\"LuisArias\"");
    assertTrue(matcher.matches());
    assertEquals("LuisArias", matcher.group(1));
  }

  public void testParsePasswordDigest() {
    Pattern pattern = Pattern.compile(WSSEHttpHeader.getPasswordDigestExpression());
    Matcher matcher = pattern.matcher("PasswordDigest=\"quR/EWLAV4xLf9Zqyw4pDmfV9OY=\"");
    assertTrue(matcher.matches());
    assertEquals("quR/EWLAV4xLf9Zqyw4pDmfV9OY=", matcher.group(1));
  }
  
  public void testParseCreated() {
    Pattern pattern = Pattern.compile(WSSEHttpHeader.getCreatedExpression());
    Matcher matcher = pattern.matcher("Created=\"2003-12-15T14:43:07Z\"");
    assertTrue(matcher.matches());
    assertEquals("2003-12-15T14:43:07Z", matcher.group(1));
  }  

  public void testParseNonce() {
    Pattern pattern = Pattern.compile(WSSEHttpHeader.getNonceExpression());
    Matcher matcher = pattern.matcher("Nonce=\"d36e316282959a9ed4c89851497a717f\"");
    assertTrue(matcher.matches());
    assertEquals("d36e316282959a9ed4c89851497a717f", matcher.group(1));
  }  

  public void testParseHttpHeaderNotAuthenticated() throws LifeblogServiceException {
    String nonce = "d36e316282959a9ed4c89851497a717f";
    String created = "2003-12-15T14:43:07Z";
    String password = "Toto";
    String passwordDigest = new String(Base64.encodeBase64(DigestUtils.sha(nonce + created + password)));

    String httpHeader = "UsernameToken Username=\"LuisArias\", PasswordDigest=\"quR/EWLAV4xLf9Zqyw4pDmfV9OY=\", Nonce=\"d36e316282959a9ed4c89851497a717f\", Created=\"2003-12-15T14:43:07Z\"";
    
    WSSEHttpHeader wsseHeader = WSSEHttpHeader.parseHttpHeader(httpHeader);
    assertNotNull(wsseHeader);
    assertFalse(wsseHeader.isAuthenticated(password));
  }
  
  public void testParseHttpHeaderAuthenticated() throws LifeblogServiceException {
    String nonce = "d36e316282959a9ed4c89851497a717f";
    String created = "2003-12-15T14:43:07Z";
    String password = "Toto";
    String passwordDigest = new String(Base64.encodeBase64(DigestUtils.sha(nonce + created + password)));

    String httpHeader = "UsernameToken Username=\"LuisArias\", PasswordDigest=\"" + passwordDigest + "\", Nonce=\""+ nonce + "\", Created=\""+ created + "\"";
    
    WSSEHttpHeader wsseHeader = WSSEHttpHeader.parseHttpHeader(httpHeader);
    assertNotNull(wsseHeader);
    assertTrue(wsseHeader.isAuthenticated(password));
  }
  
  public void testParseHeaderInvalidHeader() throws LifeblogServiceException {
    String httpHeader = " sqdljfqsdlk dlsqfjqmlskdjf";
    
    WSSEHttpHeader wsseHeader = WSSEHttpHeader.parseHttpHeader(httpHeader);
    
    assertNull(wsseHeader);
  }
  
  public void testParseCreatedDate() {
    String created = "2003-12-15T14:43:07Z";
    WSSEHttpHeader wsseHeader = new WSSEHttpHeader();
    wsseHeader.setCreated(created);
    
    Calendar cal = wsseHeader.parseCreated();
    
    assertNotNull(cal);
    assertEquals(cal.get(Calendar.YEAR), 2003);
    assertEquals(cal.get(Calendar.MONTH) + (1 - Calendar.JANUARY), 12);
    assertEquals(cal.get(Calendar.DAY_OF_MONTH), 15);
    assertEquals(cal.get(Calendar.HOUR_OF_DAY), 14);
    assertEquals(cal.get(Calendar.MINUTE), 43);
    assertEquals(cal.get(Calendar.SECOND), 7);
  }
  
  public void testCalendarToW3CDSTFormat() {
    Calendar cal = Calendar.getInstance();
    String created = WSSEHttpHeader.CalendarToW3CDSTFormat(cal);
    assertNotNull(created);
    
    Calendar cal2 = WSSEHttpHeader.parseCreated(created);
    
    assertNotNull(cal2);
    assertEquals(cal.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
    assertEquals(cal.get(Calendar.MONTH), cal2.get(Calendar.MONTH));
    assertEquals(cal.get(Calendar.DAY_OF_MONTH), cal2.get(Calendar.DAY_OF_MONTH));
    assertEquals(cal.get(Calendar.HOUR_OF_DAY), cal2.get(Calendar.HOUR_OF_DAY));
    assertEquals(cal.get(Calendar.MINUTE), cal2.get(Calendar.MINUTE));
    assertEquals(cal.get(Calendar.SECOND), cal2.get(Calendar.SECOND));
  }
}
