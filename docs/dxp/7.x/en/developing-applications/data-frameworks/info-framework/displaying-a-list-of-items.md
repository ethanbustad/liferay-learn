# Displaying A List of Items

[
Start with context in our docs:
Liferay's Info Framework provides ___ that you can use to ___
As part of ___ you can use this framework for retrieving and displaying a list of items
The following tutorial provides a sample implementation of the `InfoListProvider` that ___
]

In Liferay 7.3 we have introduced new concepts such as Collection Pages and a new type of fragment called Collection Display Fragments.

In this document we'll show you step by step the classes and logic that need to be implemented to allow us to create a Collection Page for a custom entity, and also to list our items in a Collection Display Fragment, since they go hand in hand.

[
Except.... we need more than just this one class in order to do either of those things. This one class serves up the list (i.e. provides the backend), but the items still _don't have any render logic_. How is that logic supplied? The answer must lie in the other drafts supplied by Jurgen.
]

[
annotation: if you want to use one of these, use this other thing...
]
[
talk to the team -- maybe get a one-line description of what each class does
]

[
include a deployment section for users to download, build, deploy the example
instruct the user on how to confirm that the module has successfully deployed
]

## Displaying the List of Items

Retrieving the list of items is very straightforward. We just need to implement `InfoListProvider` for our entity. If we inspect the interface, we'll see that we have 4 methods at our disposal. The first two retrieve a list of items, the third one retrieves a count of items, and the fourth supplies the label that we'll display.

The first method to retrieve the list receives an `InfoListProviderContext` parameter that's populated based on the page that calls our `InfoListProvider`. It contains information about the company, group, and page (among other things). The second method receives not only that context data, but also parameters regarding pagination and sorting.

```java
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

        try {
            return _userService.getGroupUsers(
                group.getGroupId(), WorkflowConstants.STATUS_APPROVED,
                pagination.getStart(), pagination.getEnd(), null);
        }
        catch (PortalException portalException) {
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
            return 0;
        }
    }

    @Override
    public String getLabel(Locale locale) {
        return LanguageUtil.get(locale, "users");
    }

    @Reference
    private UserService _userService;

}
```

Once this is deployed you'll be able to confirm it's working properly by selecting it as the data source for a Collection Page:

1. Go to the site menu > Site Builder > Pages
1. Click the Add icon (+) next to Public Pages and select Add Collection Page
1. Select the Collection Providers tab at the top
1. Find and select your provider from the list
1. Select Blank as the template
1. Give the page a name

You should then see a simple page with a few items listed, served up by your `InfoListProvider`.

Publishing the page at this stage will lead to the page being blank -- this is because there is no renderer for the supplied items, so they can't be displayed even though they're being provided properly by your `InfoListProvider`. Similarly, the provider is not yet available to select for a Collection Display Fragment.

[
Go to Site Builder > Collections > Collection Providers tab to confirm your new provider is deployed and working properly. From there, you can view what data is being supplied by clicking View Items in the action menu for your provider's row.
]

[
Create a Collection Page, select your provider, give the page a name, and you should see your items populated (very simplistically).
]

[
bridge to another article at the beginning or the end can make sense if they're super related, and should go in a certain order
]

[
some other articles are small like this -- super small but there is value
]

[
Alec's articles to cross link to: displaying-content/displaying-collections/developer-guide/: https://github.com/sez11a/liferay-learn/pull/305
]

## Mapping of Fields

To take full advantage of the Collection Display Fragment, we should allow Users to _map_ the fields (that is, select which fields to display and how to display them) for the object that we selected. In order to achieve this, we just have to implement a few classes.
