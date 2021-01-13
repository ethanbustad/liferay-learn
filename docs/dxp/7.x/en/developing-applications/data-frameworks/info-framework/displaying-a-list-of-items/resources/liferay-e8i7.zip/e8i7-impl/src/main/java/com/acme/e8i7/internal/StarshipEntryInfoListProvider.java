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