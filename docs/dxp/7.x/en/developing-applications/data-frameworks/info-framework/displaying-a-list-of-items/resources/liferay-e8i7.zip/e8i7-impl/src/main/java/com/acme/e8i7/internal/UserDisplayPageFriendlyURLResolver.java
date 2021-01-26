package com.acme.e8i7.internal;

import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.asset.display.page.portlet.BaseAssetDisplayPageFriendlyURLResolver;

import org.osgi.service.component.annotations.Component;

@Component(service = FriendlyURLResolver.class)
public class UserDisplayPageFriendlyURLResolver
	extends BaseAssetDisplayPageFriendlyURLResolver {

	@Override
	public String getURLSeparator() {
		return "/u/";
	}

}