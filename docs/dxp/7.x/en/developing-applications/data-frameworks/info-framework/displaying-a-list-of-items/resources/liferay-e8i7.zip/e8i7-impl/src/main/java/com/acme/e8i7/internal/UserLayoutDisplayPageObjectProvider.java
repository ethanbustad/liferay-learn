package com.acme.e8i7.internal;

import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.Portal;

import java.util.Locale;

public class UserLayoutDisplayPageObjectProvider
	implements LayoutDisplayPageObjectProvider<User> {

	public UserLayoutDisplayPageObjectProvider(User user, Portal portal) {
		_user = user;
		_portal = portal;
	}

	@Override
	public long getClassNameId() {
		return _portal.getClassNameId(User.class.getName());
	}

	@Override
	public long getClassPK() {
		return _user.getUserId();
	}

	@Override
	public long getClassTypeId() {
		return 0;
	}

	@Override
	public String getDescription(Locale locale) {
		return StringBundler.concat(
			_user.getFullName(), StringPool.COMMA_AND_SPACE,
			_user.getScreenName(), StringPool.COMMA_AND_SPACE,
			_user.getEmailAddress());
	}

	@Override
	public User getDisplayObject() {
		return _user;
	}

	@Override
	public long getGroupId() {
		return _user.getGroupId();
	}

	@Override
	public String getKeywords(Locale locale) {
		return null;
	}

	@Override
	public String getTitle(Locale locale) {
		return _user.getFullName();
	}

	@Override
	public String getURLTitle(Locale locale) {
		return _user.getScreenName();
	}

	private final Portal _portal;
	private final User _user;

}