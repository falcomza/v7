package uk.co.q3c.v7.base.navigate;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.text.Collator;
import java.util.Locale;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.fest.assertions.Fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import uk.co.q3c.v7.base.navigate.sitemap.Sitemap;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapNode;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapService;
import uk.co.q3c.v7.base.navigate.sitemap.SitemapURIConverter;
import uk.co.q3c.v7.base.shiro.DefaultURIPermissionFactory;
import uk.co.q3c.v7.base.shiro.LoginStatusHandler;
import uk.co.q3c.v7.base.shiro.SubjectProvider;
import uk.co.q3c.v7.base.ui.ScopedUI;
import uk.co.q3c.v7.base.view.ErrorView;
import uk.co.q3c.v7.base.view.LoginView;
import uk.co.q3c.v7.base.view.LogoutView;
import uk.co.q3c.v7.base.view.V7View;
import uk.co.q3c.v7.base.view.V7ViewChangeEvent;
import uk.co.q3c.v7.base.view.V7ViewChangeListener;
import uk.co.q3c.v7.i18n.LabelKey;
import uk.co.q3c.v7.i18n.Translate;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.mycila.testing.junit.MycilaJunitRunner;
import com.mycila.testing.plugin.guice.GuiceContext;
import com.vaadin.server.Page;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

@RunWith(MycilaJunitRunner.class)
@GuiceContext({})
public class DefaultV7NavigatorTest {

	static class View2 implements V7View {

		@Override
		public void enter(V7ViewChangeEvent event) {
		}

		@Override
		public Component getRootComponent() {

			return null;
		}

	}

	static class View1 implements V7View {

		@Override
		public void enter(V7ViewChangeEvent event) {
		}

		@Override
		public Component getRootComponent() {

			return null;
		}

	}

	DefaultV7Navigator navigator;

	@Mock
	Provider<ErrorView> errorViewProvider;

	StrictURIFragmentHandler uriHandler;

	@Mock
	ScopedUI scopedUI;

	@Mock
	LoginView loginView;

	@Mock
	LogoutView logoutView;

	@Mock
	V7View previousView;

	@Mock
	View1 view1;

	@Mock
	View2 view2;

	@Mock
	V7View privateHomeView;

	@Mock
	Injector injector;

	@Mock
	Page page;

	@Mock
	ErrorView errorView;

	@Mock
	V7ViewChangeListener listener1;

	@Mock
	V7ViewChangeListener listener2;

	@Mock
	V7ViewChangeListener listener3;

	@Mock
	Sitemap sitemap;

	@Mock
	SitemapService sitemapService;

	@Mock
	DefaultURIPermissionFactory uriPermissionFactory;

	@Mock
	SitemapURIConverter sitemapURIConverter;

	@Mock
	LoginStatusHandler loginHandler;

	@Mock
	SubjectProvider subjectProvider;

	@Mock
	Subject subject;

	@Mock
	Collator collator;

	@Mock
	Translate translate;

	// had some issues with mocking this - the getViewClass() method wouldn't play
	// so resorted to old fashioned mocking
	SitemapNode mockNode;
	SitemapNode mockNode2;

	@Before
	public void setup() {
		// ini = iniPro.get();
		// ini.validate();

		// sitemap = new TextReaderSitemapProvider(new StandardPageBuilder()).get();

		uriHandler = new StrictURIFragmentHandler();
		mockNode = new SitemapNode();
		mockNode2 = new SitemapNode();

		when(sitemapService.getSitemap()).thenReturn(sitemap);
		when(scopedUI.getPage()).thenReturn(page);
		when(errorViewProvider.get()).thenReturn(errorView);
		when(subjectProvider.get()).thenReturn(subject);
		when(sitemapURIConverter.pageIsPublic(anyString())).thenReturn(true);
		when(injector.getInstance(LogoutView.class)).thenReturn(logoutView);
		when(injector.getInstance(LoginView.class)).thenReturn(loginView);
		when(injector.getInstance(View2.class)).thenReturn(view2);
		when(injector.getInstance(View1.class)).thenReturn(view1);

		navigator = new DefaultV7Navigator(injector, errorViewProvider, uriHandler, sitemapService, subjectProvider,
				uriPermissionFactory, sitemapURIConverter, loginHandler);
		CurrentInstance.set(UI.class, scopedUI);
	}

	@Test
	public void logout() {

		// given
		String page = "public/logout";
		when(sitemap.standardPageURI(StandardPageKey.Logout)).thenReturn(page);
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(sitemapURIConverter.nodeForUri(page, false)).thenReturn(mockNode);
		mockNode.setViewClass(LogoutView.class);
		// when
		navigator.navigateTo(StandardPageKey.Logout);
		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(LogoutView.class);
		verify(scopedUI).changeView(null, logoutView);
	}

	@Test
	public void login() {
		// given
		String page = "public/login";
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(sitemapURIConverter.nodeForUri(page, false)).thenReturn(mockNode);
		mockNode.setViewClass(LoginView.class);
		// when
		navigator.navigateTo(page);
		// then

		assertThat(navigator.getCurrentView()).isInstanceOf(LoginView.class);
		verify(scopedUI).changeView(null, loginView);

	}

	@Test
	public void loginSuccessFul_toPreviousView() {

		// given
		String page = "public/view2";
		String page2 = "public/login";

		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(sitemap.getRedirectFor(page2)).thenReturn(page2);

		when(sitemapURIConverter.nodeForUri(page, false)).thenReturn(mockNode);
		when(sitemapURIConverter.nodeForUri(page2, false)).thenReturn(mockNode2);

		mockNode.setViewClass(View2.class);
		mockNode2.setViewClass(LoginView.class);

		when(sitemap.standardPageURI(StandardPageKey.Login)).thenReturn(page2);

		navigator.navigateTo(page);
		navigator.navigateTo(StandardPageKey.Login);
		// when
		navigator.loginSuccessful();
		// then
		assertThat(navigator.getNavigationState()).isEqualTo(page);
	}

	@Test
	public void loginSuccessFul_noPreviousView() {

		// given
		String page = "private";
		when(sitemap.standardPageURI(StandardPageKey.Private_Home)).thenReturn(page);
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(sitemapURIConverter.nodeForUri(page, false)).thenReturn(mockNode);
		mockNode.setViewClass(View2.class);

		navigator.setCurrentView(loginView, "xx", "yy");
		navigator.setPreviousView(null);
		// when
		navigator.loginSuccessful();
		// then
		verify(scopedUI).changeView(loginView, view2);

	}

	@Test
	public void navigateTo() {

		// given
		String page = "public/view2";
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(sitemapURIConverter.nodeForUri(page, false)).thenReturn(mockNode);
		mockNode.setViewClass(View2.class);

		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentView()).isEqualTo(view2);

	}

	@Test
	public void navigateToEmptyPageWithParams() {

		// given
		String page1 = "";
		String fragment1 = page1 + "/id=2/age=5";
		when(sitemap.getRedirectFor(page1)).thenReturn("public");
		when(sitemapURIConverter.nodeForUri("public/id=2/age=5", false)).thenReturn(mockNode);
		mockNode.setViewClass(View1.class);

		// when
		navigator.navigateTo(fragment1);
		// then
		assertThat(navigator.getNavigationState()).isEqualTo("public/id=2/age=5");

	}

	@Test(expected = InvalidURIException.class)
	public void navigateTo_invalidURI() {

		// given
		// given
		String page = "public/view3";
		when(sitemap.getRedirectFor(page)).thenReturn(page);

		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getCurrentView()).isEqualTo(errorView);

	}

	@Test
	public void getNavigationState() {

		// given
		String page = "public/view2";
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(sitemapURIConverter.nodeForUri(page, false)).thenReturn(mockNode);
		mockNode.setViewClass(View2.class);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getNavigationState()).isEqualTo(page);

	}

	@Test
	public void getNavigationParams() {

		// given
		String page = "public/view2";
		String pageWithParams = "public/view2/id=1/age=2";
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(sitemapURIConverter.nodeForUri(pageWithParams, false)).thenReturn(mockNode);
		mockNode.setViewClass(View2.class);
		// when
		navigator.navigateTo(pageWithParams);
		// then
		assertThat(navigator.getNavigationParams()).containsOnly("id=1", "age=2");

	}

	@Test
	public void navigateToNode() {

		// given
		String page = "public/view2";
		mockNode = new SitemapNode(page, view2.getClass(), LabelKey.Cancel, Locale.UK, collator, translate);
		when(sitemap.uri(mockNode)).thenReturn(page);
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(sitemapURIConverter.nodeForUri(page, false)).thenReturn(mockNode);
		mockNode.setViewClass(View2.class);

		// when
		navigator.navigateTo(mockNode);
		// then
		assertThat(navigator.getNavigationState()).isEqualTo(page);

	}

	@Test
	public void currentAndPreviousViews_andClearHistory() {

		// given
		String page1 = "view1";
		String fragment1 = page1 + "/id=1";
		when(sitemap.getRedirectFor(page1)).thenReturn(page1);
		when(sitemapURIConverter.nodeForUri(fragment1, false)).thenReturn(mockNode);
		mockNode.setViewClass(View1.class);

		String page2 = "view2";
		String fragment2 = page2 + "/id=2";
		when(sitemap.getRedirectFor(page2)).thenReturn(page2);
		when(sitemapURIConverter.nodeForUri(fragment2, false)).thenReturn(mockNode2);
		mockNode2.setViewClass(View2.class);

		// when

		// then
		// start position
		assertThat(navigator.getCurrentView()).isNull();
		assertThat(navigator.getCurrentViewName()).isNull();
		assertThat(navigator.getNavigationState()).isNull();

		assertThat(navigator.getPreviousView()).isNull();
		assertThat(navigator.getPreviousViewName()).isNull();
		assertThat(navigator.getPreviousFragment()).isNull();

		// when
		navigator.navigateTo(fragment1);

		// then
		assertThat(navigator.getCurrentView()).isEqualTo(view1);
		assertThat(navigator.getCurrentViewName()).isEqualTo(page1);
		assertThat(navigator.getNavigationState()).isEqualTo(fragment1);

		assertThat(navigator.getPreviousView()).isNull();
		assertThat(navigator.getPreviousViewName()).isNull();
		assertThat(navigator.getPreviousFragment()).isNull();

		// when
		navigator.navigateTo(fragment2);

		// then
		assertThat(navigator.getCurrentView()).isEqualTo(view2);
		assertThat(navigator.getCurrentViewName()).isEqualTo(page2);
		assertThat(navigator.getNavigationState()).isEqualTo(fragment2);

		assertThat(navigator.getPreviousView()).isEqualTo(view1);
		assertThat(navigator.getPreviousViewName()).isEqualTo(page1);
		assertThat(navigator.getPreviousFragment()).isEqualTo(fragment1);

		// when
		navigator.clearHistory();

		// then
		assertThat(navigator.getCurrentView()).isEqualTo(view2);
		assertThat(navigator.getCurrentViewName()).isEqualTo(page2);
		assertThat(navigator.getNavigationState()).isEqualTo(fragment2);

		assertThat(navigator.getPreviousView()).isNull();
		assertThat(navigator.getPreviousViewName()).isNull();
		assertThat(navigator.getPreviousFragment()).isNull();
	}

	/**
	 * Checks add and remove listeners
	 */
	@Test
	public void listeners_allRespond() {

		// given
		String page = "public/view2";
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(sitemapURIConverter.nodeForUri(page, false)).thenReturn(mockNode);
		mockNode.setViewClass(View2.class);

		// need to return true, or first listener will block the second
		when(listener1.beforeViewChange(any(V7ViewChangeEvent.class))).thenReturn(true);
		navigator.addViewChangeListener(listener1);
		navigator.addViewChangeListener(listener2);
		navigator.addViewChangeListener(listener3);
		// when
		navigator.removeViewChangeListener(listener3);
		navigator.navigateTo(page);
		// then
		verify(listener1, times(1)).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener2, times(1)).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener3, never()).beforeViewChange(any(V7ViewChangeEvent.class));
	}

	@Test
	public void listener_blocked() {

		// given
		String page = "public/view2";
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(sitemapURIConverter.nodeForUri(page, false)).thenReturn(mockNode);
		mockNode.setViewClass(View2.class);
		// to block second and subsequent
		when(listener1.beforeViewChange(any(V7ViewChangeEvent.class))).thenReturn(false);
		navigator.addViewChangeListener(listener1);
		navigator.addViewChangeListener(listener2);
		navigator.addViewChangeListener(listener3);
		// when
		navigator.navigateTo(page);
		// then
		verify(listener1, times(1)).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener2, never()).beforeViewChange(any(V7ViewChangeEvent.class));
		verify(listener3, never()).beforeViewChange(any(V7ViewChangeEvent.class));
	}

	@Test
	public void redirection() {

		// given
		String page = "wiggly";
		String page2 = "private/transfers";

		when(sitemap.getRedirectFor(page)).thenReturn(page2);
		when(sitemapURIConverter.nodeForUri(page2, false)).thenReturn(mockNode);
		mockNode.setViewClass(View2.class);
		// when
		navigator.navigateTo(page);
		// then
		assertThat(navigator.getNavigationState()).isEqualTo(page2);
	}

	@Test(expected = AuthorizationException.class)
	public void privatePage() {

		// given
		String page = "public/view2";
		when(sitemap.getRedirectFor(page)).thenReturn(page);
		when(sitemapURIConverter.nodeForUri(page, false)).thenReturn(mockNode);
		mockNode.setViewClass(View2.class);
		when(sitemapURIConverter.pageIsPublic(anyString())).thenReturn(false);
		// when
		navigator.navigateTo(page);
		// then
		Fail.fail("Exception was expected");

	}

	@Test
	public void error() {

		// given

		// when
		navigator.error();
		// then
		assertThat(navigator.getCurrentView()).isInstanceOf(ErrorView.class);

	}
}
