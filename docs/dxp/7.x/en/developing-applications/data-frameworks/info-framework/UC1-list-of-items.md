# Creating an Information List Provider

To demonstrate Information List Providers, follow the instructions below to implement an `InfoListProvider` for the most viewed asset entries. In this case  the list shows a list of `AssetEntry` instances. Since they already have their own renderer, they can appear in the Asset Publisher with no additional changes. If you create a provider for a custom class, you must also render it.

1.  Create a [module](/docs/7-2/reference/-/knowledge_base/r/creating-a-project) named asset-entry-info-list-provider.

1.  Create a package inside the module named `com.liferay.docs.info.provider`.

1.  Inside the package, create a class named `AssetEntryInfoListProvider` that implements `InfoListProvider` and registers it as a component:

    ```java
    @Component(service = InfoListProvider.class)
    public class AssetEntryInfoListProvider
        implements InfoListProvider<AssetEntry> {
    }
    ```

1.  Next, add the necessary `@Reference` that you need for the logic of retrieving assets to the bottom of the class.

    ```java
        @Reference
        private AssetEntryLocalService _assetEntryLocalService;
    ```

1.  Then implement `getInfoList` which returns just the list.

    ```java
        @Override
        public List<AssetEntry> getInfoList(
            InfoListProviderContext infoListProviderContext) {

            return _assetEntryLocalService.getTopViewedEntries(
                new String[0], false, 0, 20);
        }
    ```

    Descending order and a maximum of 20 items to return is hardcoded. 

1.  Now implement the second method, which provides greater control over how items are returned to the provider.

    ```java
        @Override
        public List<AssetEntry> getInfoList(
            InfoListProviderContext infoListProviderContext, Pagination pagination,
            Sort sort) {

            return _assetEntryLocalService.getTopViewedEntries(
                new String[0], !sort.isReverse(), pagination.getStart(),
                pagination.getEnd());
        }
    ```

1.  Provide a method to get a full count of info list items. 

    ```java
        @Override
        public int getInfoListCount(
            InfoListProviderContext infoListProviderContext) {

            Company company = infoListProviderContext.getCompany();

            return _assetEntryLocalService.getCompanyEntriesCount(
                company.getCompanyId());
        }
    ```

1.  Finally, add a method that provides a display label for the list.

    ```java
        @Override
        public String getLabel(Locale locale) {
            return "Most Viewed Content";
        }
    ```

The completed class should look like this:

```java
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
```

This class is now ready to go! If you deploy it, it shows the "Most Viewed Content" in any Asset Publisher.

## Next steps

This example is pretty simplistic and probably not useful in real world cases. To begin with, you may want to scope the search to the current site. You can also add more advanced filter criteria or provide a configuration for the provider using Liferay's configuration framework.

As mentioned, it is also possible to implement providers for custom types. The following code shows a partial example of a provider for a custom `MyOrder` class:

```java
@Component(service = InfoListProvider.class)
public class MyOrderProvider implements InfoListProvider<MyOrder> {

    @Override
    public List<MyOrder> getInfoList(
        InfoListProviderContext infoListProviderContext, Pagination pagination,
        Sort sort) {

        return _myOrderLocalService.getOrders(
            [...], !sort.isReverse(), pagination.getStart(),
            pagination.getEnd());
        }

    [..]

    @Reference
    private MyOrderLocalService _myOrderLocalService;

}
```

