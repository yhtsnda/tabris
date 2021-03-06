/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * EclipseSource - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.tabris.widgets;

import static com.eclipsesource.tabris.internal.Clauses.when;
import static com.eclipsesource.tabris.internal.Clauses.whenNull;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.eclipsesource.tabris.internal.ScrollingCompositeUtil;

/**
 * <p>
 * A {@link ScrollingComposite} can be used the same way as a simple
 * {@link Composite}. The difference between a {@link Composite} and a
 * {@link ScrollingComposite} is that a {@link ScrollingComposite} shows
 * scrollbars automatically when it's content becomes to big.
 * </p>
 * <p>
 * You may noticed that there is a {@link ScrolledComposite}. A
 * {@link ScrolledComposite} is not an easy widget. In most cases it's enough to
 * have just a scrollable container for some content that grows dynamically.
 * This task can not be accomplished easily with the {@link ScrolledComposite}.
 * Thus the {@link ScrollingComposite} was created to make just the described
 * task easy and nothing more.
 * </p>
 * <p>
 * Please note: A {@link ScrollingComposite} uses a {@link ScrolledComposite} as
 * it's parent. So, if you need to get the {@link ScrolledComposite} use the
 * {@link ScrollingComposite#getParent()} method.
 * </p>
 * <p>
 * <b>Styles:</b> H_SCROLL, V_SCROLL
 * </p>
 *
 * @see Composite
 * @see ScrolledComposite
 *
 * @since 1.0
 */
public class ScrollingComposite extends Composite {

  private ScrolledComposite scrolledComposite;

  /**
   * <p>
   * Constructs a new {@link ScrollingComposite}. See
   * {@link Composite#Composite(Composite, int)} for a more detailed
   * description.
   * </p>
   *
   * @see Composite#Composite(Composite, int)
   */
  public ScrollingComposite( Composite parent, int style ) {
    super( createScrolledComposite( parent, checkStyle( style ) ), checkStyle( style ) );
    scrolledComposite = ( ScrolledComposite )this.getParent();
    initializeScrolledComposite();
  }

  private static int checkStyle( int style ) {
    int mask = SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.LEFT_TO_RIGHT;
    return style & mask;
  }

  private static ScrolledComposite createScrolledComposite( Composite parent, int style ) {
    ScrolledComposite scrolledComposite = new ScrolledComposite( parent, style );
    scrolledComposite.setExpandVertical( true );
    scrolledComposite.setExpandHorizontal( true );
    return scrolledComposite;
  }

  private void initializeScrolledComposite() {
    scrolledComposite.setContent( this );
    handleScrollbars();
    scrolledComposite.addControlListener( new ControlAdapter() {

      @Override
      public void controlResized( ControlEvent event ) {
        handleScrollbars();
        scrolledComposite.layout( true, true );
      }
    } );
  }

  /**
   * <p>
   * Returns the inner {@link ScrolledComposite}.
   * </p>
   *
   * @since 1.4
   */
  public ScrolledComposite getScrolledComposite() {
    return scrolledComposite;
  }

  @Override
  public void layout( boolean changed, boolean all ) {
    super.layout( changed, all );
    handleScrollbars();
  }

  private void handleScrollbars() {
    computeSize( SWT.DEFAULT, SWT.DEFAULT );
  }

  @Override
  public Point computeSize( int wHint, int hHint, boolean changed ) {
    Point resultSize = super.computeSize( SWT.DEFAULT, SWT.DEFAULT, changed );
    if( hasStyle( SWT.V_SCROLL ) && hasStyle( SWT.H_SCROLL ) ) {
      setMinSize( resultSize.x, resultSize.y );
    } else if( hasStyle( SWT.H_SCROLL ) ) {
      resultSize = calculateHorizontalSize( changed );
      setMinSize( resultSize.x, SWT.DEFAULT );
    } else if( hasStyle( SWT.V_SCROLL ) ) {
      resultSize = calculateVerticalSize( changed );
      setMinSize( scrolledComposite.getClientArea().width, resultSize.y );
    }
    return resultSize;
  }

  private boolean hasStyle( int flag ) {
    return ( getStyle() & flag ) == flag;
  }

  private Point calculateVerticalSize( boolean changed ) {
    Point resultSize = super.computeSize( SWT.DEFAULT, SWT.DEFAULT, changed );
    int clientAreaWidth = scrolledComposite.getClientArea().width;
    Point widthSize = super.computeSize( clientAreaWidth, SWT.DEFAULT, changed );
    if( widthSize.y > resultSize.y ) {
      resultSize = widthSize;
    }
    return resultSize;
  }

  private Point calculateHorizontalSize( boolean changed ) {
    Point resultSize = super.computeSize( SWT.DEFAULT, SWT.DEFAULT, changed );
    int clientAreaHeight = scrolledComposite.getClientArea().height;
    Point heightSize = super.computeSize( SWT.DEFAULT, clientAreaHeight, changed );
    if( heightSize.x > resultSize.x ) {
      resultSize = heightSize;
    }
    return resultSize;
  }

  private void setMinSize( int width, int height ) {
    scrolledComposite.setMinHeight( height );
    scrolledComposite.setMinWidth( width );
  }

  @Override
  public void setLayoutData( Object layoutData ) {
    checkWidget();
    scrolledComposite.setLayoutData( layoutData );
  }

  @Override
  public Object getLayoutData() {
    checkWidget();
    return scrolledComposite.getLayoutData();
  }

  /**
   * <p>
   * Scrolls to the defined control until it's visible.
   * </p>
   *
   * @param control the control to scroll to. Must not be <code>null</code>.
   * @exception IllegalArgumentException when the defined control is
   *              <code>null</code> or not a children of this Composite.
   */
  public void reveal( Control control ) {
    checkRevealState( control );
    scrolledComposite.showControl( control );
  }

  /**
   * <p>
   * Checks if a control is visible within the visible area. The control needs
   * to be completely visible for a <code>true</code> result.
   * </p>
   *
   * @param control the control to check the visibility. Must not be <code>null</code>.
   *
   * @throws IllegalArgumentException when the defined control is <code>null</code> or not a
   *            children of this Composite.
   */
  public boolean isRevealed( Control control ) {
    checkRevealState( control );
    Point origin = scrolledComposite.getOrigin();
    Rectangle clientArea = scrolledComposite.getClientArea();
    Rectangle controlBounds = control.getBounds();
    return ScrollingCompositeUtil.isRevealed( origin, clientArea, controlBounds );
  }

  private void checkRevealState( Control control ) {
    whenNull( control ).throwIllegalArgument( "Child to reveal must not be null" );
    when( !containsControl( control ) ).throwIllegalArgument( "Control is not a child" );
    checkWidget();
  }

  private boolean containsControl( Control control ) {
    boolean contains = false;
    if( control != null && !control.isDisposed() ) {
      Composite parent = control.getParent();
      while( parent != null && !( parent instanceof Shell ) && !contains ) {
        if( this == parent ) {
          contains = true;
        }
        parent = parent.getParent();
      }
    }
    return contains;
  }

  @Override
  public void dispose() {
    if( scrolledComposite != null ) {
      scrolledComposite = null;
      getParent().dispose();
    }
    super.dispose();
  }
}
