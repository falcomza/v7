package uk.co.q3c.v7.base.view;

import java.util.List;

import javax.inject.Inject;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;

@UIScoped
public class DefaultRequestSystemAccountEnableView extends StandardPageViewBase implements
		RequestSystemAccountEnableView {
	@Inject
	protected DefaultRequestSystemAccountEnableView(V7Navigator navigator) {
		super(navigator);
	}

	@Override
	protected void processParams(List<String> params) {
	}

}
