package com.acme.e8i7.internal;

import com.liferay.info.item.capability.InfoItemCapability;
import com.liferay.info.item.provider.InfoItemCapabilitiesProvider;
import com.liferay.layout.page.template.info.item.capability.DisplayPageInfoItemCapability;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = InfoItemCapabilitiesProvider.class)
public class UserInfoItemCapabilitiesProvider
	implements InfoItemCapabilitiesProvider<User> {

	@Override
	public List<InfoItemCapability> getInfoItemCapabilities() {
		return ListUtil.fromArray(_displayPageInfoItemCapability);
	}

	@Reference
	private DisplayPageInfoItemCapability _displayPageInfoItemCapability;

}