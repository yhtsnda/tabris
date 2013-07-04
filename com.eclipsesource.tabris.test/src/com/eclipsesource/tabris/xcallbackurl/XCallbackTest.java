package com.eclipsesource.tabris.xcallbackurl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import com.eclipsesource.tabris.test.TabrisTestUtil;


public class XCallbackTest {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWithNullConfiguration() {
    new XCallback( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWithNullCallback() {
    XCallback xCallback = new XCallback( mock( XCallbackConfiguration.class ) );

    xCallback.addXCallbackListener( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testFailsWithRemovingNullCallback() {
    XCallback xCallback = new XCallback( mock( XCallbackConfiguration.class ) );

    xCallback.removeXCallbackListener( null );
  }

  @Test
  public void testUsesConfigAsCallParameter() {
    RemoteObject remoteObject = TabrisTestUtil.mockRemoteObject();
    XCallbackConfiguration configuration = new XCallbackConfiguration( "foo", "bar" );
    XCallback xCallback = new XCallback( configuration );

    xCallback.call();

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass( JsonObject.class );
    verify( remoteObject ).call( eq( "call" ), captor.capture() );
    assertEquals( "foo", captor.getValue().get( "targetScheme" ).asString() );
    assertEquals( "bar", captor.getValue().get( "targetAction" ).asString() );
  }

  @Test
  public void testUsesConfigAsCallParameterWithXSource() {
    RemoteObject remoteObject = TabrisTestUtil.mockRemoteObject();
    XCallbackConfiguration configuration = new XCallbackConfiguration( "foo", "bar" );
    XCallback xCallback = new XCallback( configuration );
    configuration.setXSource( "foo" );

    xCallback.call();

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass( JsonObject.class );
    verify( remoteObject ).call( eq( "call" ), captor.capture() );
    assertEquals( "foo", captor.getValue().get( "xSource" ).asString() );
  }

  @Test
  public void testUsesConfigAsCallParameterWithActionParameters() {
    RemoteObject remoteObject = TabrisTestUtil.mockRemoteObject();
    XCallbackConfiguration configuration = new XCallbackConfiguration( "foo", "bar" );
    XCallback xCallback = new XCallback( configuration );
    configuration.addActionParameter( "foo1", "bar1" );

    xCallback.call();

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass( JsonObject.class );
    verify( remoteObject ).call( eq( "call" ), captor.capture() );
    JsonObject parameter = captor.getValue().get( "actionParameters" ).asObject();
    assertEquals( "bar1", parameter.get( "foo1" ).asString() );
  }

  @Test
  public void testUsesConfigAsCallParameterWithXSourceName() {
    RemoteObject remoteObject = TabrisTestUtil.mockRemoteObject();
    XCallbackConfiguration configuration = new XCallbackConfiguration( "foo", "bar" );
    XCallback xCallback = new XCallback( configuration );
    configuration.setXSourceName( "foo1" );

    xCallback.call();

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass( JsonObject.class );
    verify( remoteObject ).call( eq( "call" ), captor.capture() );
    assertEquals( "foo1", captor.getValue().get( "xSourceName" ).asString() );
  }

  @Test
  public void testUsesConfigAsCallParameterWithXSuccessName() {
    RemoteObject remoteObject = TabrisTestUtil.mockRemoteObject();
    XCallbackConfiguration configuration = new XCallbackConfiguration( "foo", "bar" );
    XCallback xCallback = new XCallback( configuration );
    configuration.setXSuccessName( "foo1" );

    xCallback.call();

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass( JsonObject.class );
    verify( remoteObject ).call( eq( "call" ), captor.capture() );
    assertEquals( "foo1", captor.getValue().get( "xSuccessName" ).asString() );
  }

  @Test
  public void testUsesConfigAsCallParameterWithXErrorName() {
    RemoteObject remoteObject = TabrisTestUtil.mockRemoteObject();
    XCallbackConfiguration configuration = new XCallbackConfiguration( "foo", "bar" );
    XCallback xCallback = new XCallback( configuration );
    configuration.setXErrorName( "foo1" );

    xCallback.call();

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass( JsonObject.class );
    verify( remoteObject ).call( eq( "call" ), captor.capture() );
    assertEquals( "foo1", captor.getValue().get( "xErrorName" ).asString() );
  }

  @Test
  public void testUsesConfigAsCallParameterWithXCancelName() {
    RemoteObject remoteObject = TabrisTestUtil.mockRemoteObject();
    XCallbackConfiguration configuration = new XCallbackConfiguration( "foo", "bar" );
    XCallback xCallback = new XCallback( configuration );
    configuration.setXCancelName( "foo1" );

    xCallback.call();

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass( JsonObject.class );
    verify( remoteObject ).call( eq( "call" ), captor.capture() );
    assertEquals( "foo1", captor.getValue().get( "xCancelName" ).asString() );
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testNotifiesCallbackOnSuccess() {
    XCallback xCallback = new XCallback( mock( XCallbackConfiguration.class ) );
    RemoteObject remoteObject = xCallback.getRemoteObject();
    XCallbackListener listener = mock( XCallbackListener.class );
    xCallback.addXCallbackListener( listener );
    JsonObject properties = new JsonObject();
    JsonObject parameters = new JsonObject();
    parameters.add( "foo", "bar" );
    properties.add( "parameters", parameters );

    TabrisTestUtil.dispatchNotify( remoteObject, "OnSuccess", properties );

    ArgumentCaptor<Map> captor = ArgumentCaptor.forClass( Map.class );
    verify( listener ).onSuccess( captor.capture() );
    assertEquals( 1, captor.getValue().size() );
    assertEquals( "bar", captor.getValue().get( "foo" ) );
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testNotifiesAllCallbacksOnSuccess() {
    XCallback xCallback = new XCallback( mock( XCallbackConfiguration.class ) );
    RemoteObject remoteObject = xCallback.getRemoteObject();
    XCallbackListener listener = mock( XCallbackListener.class );
    XCallbackListener listener2 = mock( XCallbackListener.class );
    xCallback.addXCallbackListener( listener );
    xCallback.addXCallbackListener( listener2 );

    TabrisTestUtil.dispatchNotify( remoteObject, "OnSuccess", null );

    InOrder order = inOrder( listener, listener2 );
    order.verify( listener ).onSuccess( anyMap() );
    order.verify( listener2 ).onSuccess( anyMap() );
  }

  @Test
  public void testNotifiesCallbackOnError() {
    XCallback xCallback = new XCallback( mock( XCallbackConfiguration.class ) );
    RemoteObject remoteObject = xCallback.getRemoteObject();
    XCallbackListener listener = mock( XCallbackListener.class );
    xCallback.addXCallbackListener( listener );
    JsonObject parameters = new JsonObject();
    parameters.add( "errorCode", "42" );
    parameters.add( "errorMessage", "foo" );

    TabrisTestUtil.dispatchNotify( remoteObject, "OnError", parameters );

    verify( listener ).onError( "42", "foo" );
  }

  @Test
  public void testNotifiesAllCallbacksOnError() {
    XCallback xCallback = new XCallback( mock( XCallbackConfiguration.class ) );
    RemoteObject remoteObject = xCallback.getRemoteObject();
    XCallbackListener listener = mock( XCallbackListener.class );
    XCallbackListener listener2 = mock( XCallbackListener.class );
    xCallback.addXCallbackListener( listener );
    xCallback.addXCallbackListener( listener2 );
    JsonObject parameters = new JsonObject();
    parameters.add( "errorCode", "42" );
    parameters.add( "errorMessage", "foo" );

    TabrisTestUtil.dispatchNotify( remoteObject, "OnError", parameters );

    InOrder order = inOrder( listener, listener2 );
    order.verify( listener ).onError( "42", "foo" );
    order.verify( listener2 ).onError( "42", "foo" );
  }

  @Test
  public void testNotifiesCallbackOnCancel() {
    XCallback xCallback = new XCallback( mock( XCallbackConfiguration.class ) );
    RemoteObject remoteObject = xCallback.getRemoteObject();
    XCallbackListener listener = mock( XCallbackListener.class );
    xCallback.addXCallbackListener( listener );

    TabrisTestUtil.dispatchNotify( remoteObject, "OnCancel", null );

    verify( listener ).onCancel();
  }

  @Test
  public void testNotifiesAllCallbacksOnCancel() {
    XCallback xCallback = new XCallback( mock( XCallbackConfiguration.class ) );
    RemoteObject remoteObject = xCallback.getRemoteObject();
    XCallbackListener listener = mock( XCallbackListener.class );
    XCallbackListener listener2 = mock( XCallbackListener.class );
    xCallback.addXCallbackListener( listener );
    xCallback.addXCallbackListener( listener2 );

    TabrisTestUtil.dispatchNotify( remoteObject, "OnCancel", null );

    InOrder order = inOrder( listener, listener2 );
    order.verify( listener ).onCancel();
    order.verify( listener2 ).onCancel();
  }

}