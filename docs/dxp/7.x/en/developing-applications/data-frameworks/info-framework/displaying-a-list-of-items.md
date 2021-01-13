# Displaying A List of Items

In Liferay 7.3 we have introduced new concepts such as Collection Pages and a new type of fragment called Collection Display Fragments.

In this document we'll show you step by step the classes and logic that need to be implemented to allow us to create a collection page for our entity, and also to list our items in a Collection Display Fragment, since they go hand in hand. We'll use an entity called `StarshipEntry` with the following definition:

| Column Name          | Type   |
|----------------------|--------|
| starshipEntryId (PK) | long   |
| name                 | String |
| description          | String |
| createDate           | Date   |
| modifiedDate         | Date   |

## Mapping of Fields

To take full advantage of the Collection Display Fragment, we should allow Users to _map_ the fields (that is, select which fields to display and how to display them) for the object that we selected. In order to achieve this, we just have to implement a few classes.

## Displaying the List of Items

Retrieving the list of items is very straightforward. We just need to implement `InfoListProvider` for our entity. If we inspect the interface, we'll see that we have 4 methods at our disposal. The first two retrieve a list of items, the third one retrieves a count of items, and the fourth supplies the label that we'll display to the user.

The first method to retrieve the list receives an `InfoListProviderContext` parameter that's populated based on the page that calls our `InfoListProvider`. It contains information about the company, group, and page (among other things). The second method receives not only that context data, but also parameters regarding pagination and sorting.

```java
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
```

Once this is deployed we'll be able to select the `InfoListProvider` under _Content Provider_ when selecting the data source for a Collection Display Fragment. We'll also be able to create Collection Pages for it.