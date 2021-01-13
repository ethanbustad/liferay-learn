package com.acme.e8i7.internal;

import com.liferay.info.list.provider.InfoListProvider;
import com.liferay.info.list.provider.InfoListProviderContext;
import com.liferay.info.pagination.Pagination;
import com.liferay.info.sort.Sort;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.util.ResourceBundleLoader;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, service = InfoListProvider.class)
public class StarshipEntryInfoListProvider
	implements InfoListProvider<StarshipEntry> {

	@Override
	public List<StarshipEntry> getInfoList(
		InfoListProviderContext infoListProviderContext) {

		return getInfoList(infoListProviderContext, null, null);
	}

	@Override
	public List<StarshipEntry> getInfoList(
		InfoListProviderContext infoListProviderContext, Pagination pagination,
		Sort sort) {

		Optional<Group> groupOptional =
			infoListProviderContext.getGroupOptional();

		Group group = groupOptional.get();

		return _starshipEntryLocalService.getStarshipEntries(
			group.getGroupId(), pagination.getStart(), pagination.getEnd());
	}

	@Override
	public int getInfoListCount(
		InfoListProviderContext infoListProviderContext) {

		Optional<Group> groupOptional =
			infoListProviderContext.getGroupOptional();

		Group group = groupOptional.get();

		return _starshipEntryLocalService.getStarshipEntriesCount(
			group.getGroupId());
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle =
			_resourceBundleLoader.loadResourceBundle(locale);

		return LanguageUtil.get(resourceBundle, "starship-entries");
	}

	@Reference(target = "(bundle.symbolic.name=com.liferay.starship.service)")
	private ResourceBundleLoader _resourceBundleLoader;

	@Reference
	private StarshipEntryLocalService _starshipEntryLocalService;

}