package com.acme.e8i7.internal;

import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.Portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, service = LayoutDisplayPageProvider.class)
public class UserLayoutDisplayPageProvider
	implements LayoutDisplayPageProvider<User> {

	@Override
	public String getClassName() {
		return User.class.getName();
	}

	@Override
	public LayoutDisplayPageObjectProvider<User>
		getLayoutDisplayPageObjectProvider(
			InfoItemReference infoItemReference) {

		try {
			User user = _userService.getUserById(
				infoItemReference.getClassPK());

			return new UserLayoutDisplayPageObjectProvider(user, _portal);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get User with classPK " +
						infoItemReference.getClassPK(),
					portalException);
			}
		}

		return null;
	}

	@Override
	public LayoutDisplayPageObjectProvider<User>
		getLayoutDisplayPageObjectProvider(long groupId, String urlTitle) {

		try {
			Group group = _groupLocalService.getGroup(groupId);

			User user = _userService.getUserByScreenName(
				group.getCompanyId(), urlTitle);

			return new UserLayoutDisplayPageObjectProvider(user, _portal);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get User with urlTitle " + urlTitle,
					portalException);
			}
		}

		return null;
	}

	@Override
	public String getURLSeparator() {
		return "/u/";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserLayoutDisplayPageProvider.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private UserService _userService;

}