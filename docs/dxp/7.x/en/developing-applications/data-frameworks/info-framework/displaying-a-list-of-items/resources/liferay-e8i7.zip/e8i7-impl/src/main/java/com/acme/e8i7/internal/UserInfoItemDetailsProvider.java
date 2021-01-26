package com.acme.e8i7.internal;

import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.portal.kernel.model.User;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

@Component(
	immediate = true, property = Constants.SERVICE_RANKING + ":Integer=10",
	service = InfoItemDetailsProvider.class
)
public class UserInfoItemDetailsProvider
	implements InfoItemDetailsProvider<User> {

	@Override
	public InfoItemClassDetails getInfoItemClassDetails() {
		return new InfoItemClassDetails(User.class.getName());
	}

	@Override
	public InfoItemDetails getInfoItemDetails(User user) {
		return new InfoItemDetails(
			getInfoItemClassDetails(),
			new InfoItemReference(User.class.getName(), user.getUserId()));
	}

}