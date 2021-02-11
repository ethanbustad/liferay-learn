package com.acme.e8i7.internal;

import com.liferay.info.list.provider.InfoListProvider;
import com.liferay.info.list.provider.InfoListProviderContext;
import com.liferay.info.pagination.Pagination;
import com.liferay.info.sort.Sort;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, service = InfoListProvider.class)
public class UserInfoListProvider implements InfoListProvider<User> {

	@Override
	public List<User> getInfoList(
		InfoListProviderContext infoListProviderContext) {

		return getInfoList(
			infoListProviderContext,
			Pagination.of(SearchContainer.DEFAULT_DELTA, 0),
			new Sort("lastName", false));
	}

	@Override
	public List<User> getInfoList(
		InfoListProviderContext infoListProviderContext, Pagination pagination,
		Sort sort) {

		Optional<Group> groupOptional =
			infoListProviderContext.getGroupOptional();

		Group group = groupOptional.get();

		OrderByComparator<User> orderByComparator = null;

		if (sort != null) {
			orderByComparator = OrderByComparatorFactoryUtil.create(
				"User_", sort.getFieldName(), !sort.isReverse());
		}

		try {
			return _userService.getGroupUsers(
				group.getGroupId(), WorkflowConstants.STATUS_APPROVED,
				pagination.getStart(), pagination.getEnd(), orderByComparator);
		}
		catch (PortalException portalException) {
			portalException.printStackTrace();

			return Collections.emptyList();
		}
	}

	@Override
	public int getInfoListCount(
		InfoListProviderContext infoListProviderContext) {

		Optional<Group> groupOptional =
			infoListProviderContext.getGroupOptional();

		Group group = groupOptional.get();

		try {
			return _userService.getGroupUsersCount(
				group.getGroupId(), WorkflowConstants.STATUS_APPROVED);
		}
		catch (PortalException portalException) {
			_log.error(portalException, portalException);
		}

		return 0;
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "users");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserInfoListProvider.class);

	@Reference
	private UserService _userService;

}