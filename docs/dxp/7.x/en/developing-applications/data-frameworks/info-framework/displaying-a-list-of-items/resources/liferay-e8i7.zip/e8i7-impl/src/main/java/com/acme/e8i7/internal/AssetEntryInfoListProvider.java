package com.acme.e8i7.internal;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.info.list.provider.InfoListProvider;
import com.liferay.info.list.provider.InfoListProviderContext;
import com.liferay.info.pagination.Pagination;
import com.liferay.info.sort.Sort;
import com.liferay.portal.kernel.model.Company;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = InfoListProvider.class)
public class AssetEntryInfoListProvider
	implements InfoListProvider<AssetEntry> {

	@Override
	public List<AssetEntry> getInfoList(
		InfoListProviderContext infoListProviderContext) {

		return _assetEntryLocalService.getTopViewedEntries(
			new String[0], false, 0, 20);
	}

	@Override
	public List<AssetEntry> getInfoList(
		InfoListProviderContext infoListProviderContext, Pagination pagination,
		Sort sort) {

		return _assetEntryLocalService.getTopViewedEntries(
			new String[0], !sort.isReverse(), pagination.getStart(),
			pagination.getEnd());
	}

	@Override
	public int getInfoListCount(
		InfoListProviderContext infoListProviderContext) {

		Company company = infoListProviderContext.getCompany();

		return _assetEntryLocalService.getCompanyEntriesCount(
			company.getCompanyId());
	}

	@Override
	public String getLabel(Locale locale) {
		return "Most Viewed Content";
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

}