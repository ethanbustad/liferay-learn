# Display Page Template for My Custom Entity

Display Pages were introduced in Liferay 7.1, and their purpose is to provide a new level of control over the look and feel of content. Liferay provides out of the box Display Page Templates for Web Content, Blogs and Documents.

In this document we'll show you step by step the classes and logic that need to be implemented to have Display Page Templates available for any custom entity. For this, we'll use a fictional entity called `StarshipEntry`, described with the following model:

| Column Name          | Type   |
|----------------------|--------|
| starshipEntryId (PK) | long   |
| name                 | String |
| description          | String |
| createDate           | Date   |
| modifiedDate         | Date   |

## Mapping of Fields

To take full advantage of the Display Page Templates, we should allow Users to _map_ the fields (that is, select which fields to display and how to display them) for the object that we selected. In order to achieve this, we just have to implement a few classes.

[[[References another one of these docs -- figure out how to properly intertwine these articles.]]]

## Retrieve the Content for the Display Page

One key aspect of Display Pages is the specific entity associated with them, obtained by implementing `LayoutDisplayPageObjectProvider`. This class contains not only our entity, but also other metadata such as keywords, title, and description which will be used when rendering our display page:

```java
public class StarshipEntryLayoutDisplayPageObjectProvider
    implements LayoutDisplayPageObjectProvider<StarshipEntry> {

    public StarshipEntryLayoutDisplayPageObjectProvider(
        StarshipEntry starshipEntry, Portal portal) {

        _starshipEntry = starshipEntry;
        _portal = portal;
    }

    @Override
    public long getClassNameId() {
        return _portal.getClassNameId(StarshipEntry.class.getName());
    }

    @Override
    public long getClassPK() {
        return _starshipEntry.getStarshipEntryId();
    }

    @Override
    public long getClassTypeId() {
        return 0;
    }

    @Override
    public String getDescription(Locale locale) {
        return _starshipEntry.getDescription();
    }

    @Override
    public StarshipEntry getDisplayObject() {
        return _starshipEntry;
    }

    @Override
    public long getGroupId() {
        return _starshipEntry.getGroupId();
    }

    @Override
    public String getKeywords(Locale locale) {
        return null;
    }

    @Override
    public String getTitle(Locale locale) {
        return _starshipEntry.getName();
    }

    @Override
    public String getURLTitle(Locale locale) {
        return _starshipEntry.getUrlTitle();
    }

    private final Portal _portal;
    private final StarshipEntry _starshipEntry;

}
```

Now, we have to retrieve our `LayoutDisplayPageObjectProvider` implementation, and we do this by implementing `LayoutDisplayPageProvider`. Two methods retrieve it, and another method retrieves the URL separator that we are going to use to navigate to our Display Page. Keep in mind the separator since we are going to be using it in another implementation, too.

As mentioned before, there are two ways to retrieve `LayoutDisplayPageObjectProvider`. One is through an `InfoItemReference` (basically through the primary key), and the other through groupId and title; it's pretty straightforward:

```java
@Component(immediate = true, service = LayoutDisplayPageProvider.class)
public class StarshipEntryLayoutDisplayPageProvider
    implements LayoutDisplayPageProvider<StarshipEntry> {

    @Override
    public String getClassName() {
        return StarshipEntry.class.getName();
    }

    @Override
    public LayoutDisplayPageObjectProvider<StarshipEntry>
        getLayoutDisplayPageObjectProvider(
            InfoItemReference infoItemReference) {

        StarshipEntry starshipEntry = _starshipEntryService.fetchStarshipEntry(
            infoItemReference.getClassPK());

        if (starshipEntry != null) {
            return new StarshipEntryLayoutDisplayPageObjectProvider(
                starshipEntry, _portal);
        }

        if (_log.isWarnEnabled()) {
            _log.warn(
                "Unable to get Starship with classPK " +
                    infoItemReference.getClassPK());
        }

        return null;
    }

    @Override
    public LayoutDisplayPageObjectProvider<StarshipEntry>
        getLayoutDisplayPageObjectProvider(long groupId, String urlTitle) {

        try {
            StarshipEntry starshipEntry =
                _starshipEntryService.getStarshipEntry(groupId, urlTitle);

            return new StarshipEntryLayoutDisplayPageObjectProvider(
                starshipEntry, _portal);
        }
        catch (PortalException portalException) {
            if (_log.isWarnEnabled()) {
                _log.warn(
                    "Unable to get Starship with urlTitle " + urlTitle,
                    portalException);
            }
        }

        return null;
    }

    @Override
    public String getURLSeparator() {
        return "/s/";
    }

    private static final Log _log = LogFactoryUtil.getLog(
        StarshipEntryLayoutDisplayPageProvider.class);

    @Reference
    private Portal _portal;

    @Reference
    private StarshipEntryService _starshipEntryService;

}
```

## Defining Our URL Separator

We already talked about the need of an URL separator to navigate to our Display Page. To define it we need to extend `BaseAssetDisplayPageFriendlyURLResolver` and override `getURLSeparator`. When deploying it, we will be able to navigate to our Display Page through a URL such as: `[baseURL]/[separator]/[starshipEntryId]`.

For our example, the URL separator is `"/s/"`, for `StarshipEntry`.

```java
@Component(service = FriendlyURLResolver.class)
public class StarshipEntryDisplayPageFriendlyURLResolver
    extends BaseAssetDisplayPageFriendlyURLResolver {

    @Override
    public String getURLSeparator() {
        return "/s/";
    }

}
```

## Describing Our Entity

The last fundamental block for Display Pages is describing our entity. For this we have to implement the `InfoItemDetailsProvider` interface. It will describe our class, and also the entity that we are retrieving.

```java
@Component(
    immediate = true, property = Constants.SERVICE_RANKING + ":Integer=10",
    service = InfoItemDetailsProvider.class
)
public class StarshipEntryInfoItemDetailsProvider
    implements InfoItemDetailsProvider<StarshipEntry> {

    @Override
    public InfoItemClassDetails getInfoItemClassDetails() {
        return new InfoItemClassDetails(StarshipEntry.class.getName());
    }

    @Override
    public InfoItemDetails getInfoItemDetails(StarshipEntry starshipEntry) {
        return new InfoItemDetails(
            getInfoItemClassDetails(),
            new InfoItemReference(
                StarshipEntry.class.getName(),
                starshipEntry.getStarshipEntryId()));
    }

}
```

## Enabling Display Pages

Now that we have all the building blocks, we can enable Display Pages for our custom entities. To do so we have to implement `InfoItemCapabilitiesProvider`, and specify the capabilities that it supports. In this case, it would be only `DisplayPageInfoItemCapability`.

```java
@Component(service = InfoItemCapabilitiesProvider.class)
public class StarshipEntryInfoItemCapabilitiesProvider
    implements InfoItemCapabilitiesProvider<StarshipEntry> {

    @Override
    public List<InfoItemCapability> getInfoItemCapabilities() {
        return ListUtil.fromArray(_displayPageInfoItemCapability);
    }

    @Reference
    private DisplayPageInfoItemCapability _displayPageInfoItemCapability;

}
```

With this provider we'll be able to create Display Page Templates for our `StarshipEntry` entity.

## Displaying Our Item

One cool feature that only display pages have is the Display Page Content fragment. Its sole purpose is to render the item that is tied to the Display Page. Implementing is very straightforward, since we only need to implement the `InfoItemRenderer` interface.

The interface has two methods: `getLabel` and `render`. The first one is to describe our `InfoItemRenderer`, while the second one renders our item. We can use any template engine that we want in order to achieve this, or just take advantage of what we already have in the portal.

In the next example, we just render our item in a JSP:

```java
@Component(
    property = "service.ranking:Integer=200", service = InfoItemRenderer.class
)
public class StarshipEntryInfoItemRenderer
    implements InfoItemRenderer<StarshipEntry> {

    @Override
    public String getLabel(Locale locale) {
        return "Starship Entry";
    }

    @Override
    public void render(
        StarshipEntry starshipEntry, HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse) {

        try {
            httpServletRequest.setAttribute(
                StarshipConstants.STARSHIP_ENTRY, starshipEntry);

            RequestDispatcher requestDispatcher =
                _servletContext.getRequestDispatcher(
                    "/starship_entry.jsp");

            requestDispatcher.include(httpServletRequest, httpServletResponse);
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Reference(
        target = "(osgi.web.symbolicname=com.liferay.starship.web)", unbind = "-"
    )
    public void setServletContext(ServletContext servletContext) {
        _servletContext = servletContext;
    }

    private ServletContext _servletContext;

}
```

After deploying this component, and adding a Display Page Content fragment to a display page template and navigating to it, we should see our item being rendered, just like we defined in the JSP.
