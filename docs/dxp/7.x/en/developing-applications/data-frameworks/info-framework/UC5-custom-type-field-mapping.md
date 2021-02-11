# Mapping of Fields for Custom Information Types

Since Liferay 7.2 users have the ability to map fields of entities to editable fields in fragments. Out of the box, Liferay provides the ability to map fields from Web Content, Blogs and Documents and Media. Still, everyone can map the fields of their custom entities.

We'll go through all the steps and classes required to achieve this goal. To that end, we'll use a fictional entity called StarshipEntry, described with the following model:

| Column Name          | Type   |
|----------------------|--------|
| starshipEntryId (PK) | long   |
| name                 | String |
| description          | String |
| createDate           | Date   |
| modifiedDate         | Date   |

## Selecting the Item to Be Mapped

To be able to select our entities for mapping, we need to implement an `ItemSelectorView` for our entity (more info here: [Item Selector](https://help.liferay.com/hc/en-us/articles/360028725592-Item-Selector)). There are some aspects that we have to take in consideration when implementing it:

1. The main class should implement `ItemSelectorView<InfoItemItemSelectorCriterion>`
1. The method `getClassName()` should return the class name of our entity, in this case `StarshipEntry.class.getName()`
1. The method `getItemSelectorCriterionClass()` should return `InfoItemSelectorCriterion.class`
1. `getSupportedItemSelectorReturnTypes()` should contain an `InfoItemItemSelectorReturnType`
1. The `getPayload()` method of the `ItemDescriptor` should contain the `className`, `classNameId`, `classPK` and `title` fields

Other than that, we can custom fill all the other methods to return our desired values for the view. Here is an example of the implementation of the item selector for the `StarshipEntry` entity:

```java
@Component(
    property = "item.selector.view.order:Integer=200",
    service = ItemSelectorView.class
)
public class StarshipEntryItemSelectorView
    implements InfoItemSelectorView,
        ItemSelectorView<InfoItemItemSelectorCriterion> {

    @Override
    public String getClassName() {
        return StarshipEntry.class.getName();
    }

    @Override
    public Class<InfoItemItemSelectorCriterion>
        getItemSelectorCriterionClass() {

        return InfoItemItemSelectorCriterion.class;
    }

    @Override
    public List<ItemSelectorReturnType> getSupportedItemSelectorReturnTypes() {
        return _supportedItemSelectorReturnTypes;
    }

    @Override
    public String getTitle(Locale locale) {
        ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
            "content.Language", locale, getClass());

        return _language.get(resourceBundle, "starships");
    }

    @Override
    public void renderHTML(
            ServletRequest servletRequest, ServletResponse servletResponse,
            InfoItemItemSelectorCriterion infoItemItemSelectorCriterion,
            PortletURL portletURL, String itemSelectedEventName, boolean search)
        throws IOException, ServletException {

        _itemSelectorViewDescriptorRenderer.renderHTML(
            servletRequest, servletResponse, infoItemItemSelectorCriterion,
            portletURL, itemSelectedEventName, search,
            new StarshipItemSelectorViewDescriptor(
                (HttpServletRequest)servletRequest, portletURL));
    }

    private static final List<ItemSelectorReturnType>
        _supportedItemSelectorReturnTypes = Collections.singletonList(
            new InfoItemItemSelectorReturnType());

    @Reference
    private ItemSelectorViewDescriptorRenderer<InfoItemItemSelectorCriterion>
        _itemSelectorViewDescriptorRenderer;

    @Reference
    private Language _language;

    @Reference
    private Portal _portal;

    @Reference
    private StarshipEntryService _starshipEntryService;

    private class StarshipEntryItemDescriptor
        implements ItemSelectorViewDescriptor.ItemDescriptor {

        public StarshipEntryItemDescriptor(StarshipEntry starshipEntry) {
            _starshipEntry = starshipEntry;
        }

        @Override
        public String getIcon() {
            return "page";
        }

        @Override
        public String getImageURL() {
            return null;
        }

        @Override
        public String getPayload() {
            return JSONUtil.put(
                "className", StarshipEntry.class.getName()
            ).put(
                "classNameId", _portal.getClassNameId(StarshipEntry.class)
            ).put(
                "classPK", _starshipEntry.getStarshipEntryId()
            ).put(
                "title", _starshipEntry.getName()
            ).toString();
        }

        @Override
        public String getSubtitle(Locale locale) {
            return null;
        }

        @Override
        public String getTitle(Locale locale) {
            return _starshipEntry.getName();
        }

        private final StarshipEntry _starshipEntry;

    }

    private class StarshipItemSelectorViewDescriptor
        implements ItemSelectorViewDescriptor<StarshipEntry> {

        public StarshipItemSelectorViewDescriptor(
            HttpServletRequest httpServletRequest, PortletURL portletURL) {

            _httpServletRequest = httpServletRequest;
            _portletURL = portletURL;

            _portletRequest = (PortletRequest)_httpServletRequest.getAttribute(
            JavaConstants.JAVAX_PORTLET_REQUEST);

            _themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
            WebKeys.THEME_DISPLAY);
        }

        @Override
        public ItemDescriptor getItemDescriptor(StarshipEntry starshipEntry) {
            return new StarshipEntryItemDescriptor(starshipEntry);
        }

        @Override
        public ItemSelectorReturnType getItemSelectorReturnType() {
            return new InfoItemItemSelectorReturnType();
        }

        @Override
        public SearchContainer<StarshipEntry> getSearchContainer()
            throws PortalException {

            SearchContainer<StarshipEntry> searchContainer =
                new SearchContainer<>(
                    _portletRequest, _portletURL, null,
                    "no-entries-were-found");

            List<StarshipEntry> starshipEntries =
                _starshipEntryService.getStarshipEntries(
                    _themeDisplay.getScopeGroupId(), searchContainer.getStart(),
                    searchContainer.getEnd());

            searchContainer.setResults(starshipEntries);

            int total = _starshipEntryService.getStarshipEntryCount(
                _themeDisplay.getScopeGroupId());

            searchContainer.setTotal(total);

            return searchContainer;
        }

        private HttpServletRequest _httpServletRequest;
        private final PortletRequest _portletRequest;
        private final PortletURL _portletURL;
        private final ThemeDisplay _themeDisplay;

    }

}
```

## Selecting the Fields Mapped

Now that the user is able to select an entity, the next step would be to show the fields that can be mapped. In order to do this, we'll use `InfoFormProvider`, which is part of the Info Framework. The `InfoForm` has just one goal: to provide a form for a specific type of item. We could say that it describes the fields that we want the users to map. 

But before we can define our form, we have to also specify the fields that it will contain. For that, we use `InfoField`. Liferay provides different `InfoField` implementations based on the type of field that we want to map, i.e.: Text, Image, Date and many others.

For our example we want the user to be able to display the `name` (text), `description` (text) and `createDate` (date), so we can specify them like this:

```java
    public static final InfoField<TextInfoFieldType> nameInfoField =
        InfoField.builder(
        ).infoFieldType(
            TextInfoFieldType.INSTANCE
        ).name(
            "name"
        ).labelInfoLocalizedValue(
            InfoLocalizedValue.localize(
                StarshipEntryInfoItemFields.class, "name")
        ).build();
```

As you can see, every `InfoField` contains certain properties:
* The type of the field (in this case it's a text field)
* The name of the field 
* The localized label for the field

Note that name and label do not need to match with the properties from our entity. This would be a full interface for our three fields:

```java
public interface StarshipEntryInfoItemFields {

    public static final InfoField<DateInfoFieldType> createDateInfoField =
        InfoField.builder(
        ).infoFieldType(
            DateInfoFieldType.INSTANCE
        ).name(
            "createDate"
        ).labelInfoLocalizedValue(
            InfoLocalizedValue.localize(
                StarshipEntryInfoItemFields.class, "date")
        ).build();

    public static final InfoField<TextInfoFieldType> descriptionInfoField =
        InfoField.builder(
        ).infoFieldType(
            TextInfoFieldType.INSTANCE
        ).name(
            "description"
        ).labelInfoLocalizedValue(
            InfoLocalizedValue.localize(
                StarshipEntryInfoItemFields.class, "description")
        ).build();

    public static final InfoField<TextInfoFieldType> nameInfoField =
        InfoField.builder(
        ).infoFieldType(
            TextInfoFieldType.INSTANCE
        ).name(
            "name"
        ).labelInfoLocalizedValue(
            InfoLocalizedValue.localize(
                StarshipEntryInfoItemFields.class, "name")
        ).build();

}
```

Now that we have defined the fields, we can start building our `InfoForm`. It will consist of fields that can be mapped, and will also have a name. `InfoForm` provides a builder (just like `InfoField`) that will simplify the process.

```java
@Component(service = InfoItemFormProvider.class)
public class StarshipEntryInfoItemFormProvider
    implements InfoItemFormProvider<StarshipEntry> {

    @Override
    public InfoForm getInfoForm() {
        return InfoForm.builder(
        ).infoFieldSetEntry(
            StarshipEntryInfoItemFields.nameInfoField
        ).infoFieldSetEntry(
            StarshipEntryInfoItemFields.descriptionInfoField
        ).infoFieldSetEntry(
            StarshipEntryInfoItemFields.createDateInfoField
        ).name(
            StarshipEntry.class.getName()
        ).build();
    }

}
```

There are some things to notice about this. The first one is that our class is a component, and it should implement the `InfoItemFormProvider` interface with our class. That way when we want to obtain an `InfoForm` for our custom entity, the specified form will be returned.

The second one is that the order of the fields will be what we specify for the form -- in our case, name, description, and then createDate -- allowing us to specify the way we want to show the fields when Users are selecting them.

## Obtaining the Values of the Fields

Now that we have specified the form, and the fields that it contains, we can proceed to obtain their values. In order to achieve this we have to implement `InfoItemFieldValuesProvider` for our object. `InfoItemFieldValuesProvider`, as the name implies, retrieves the values of the items in a form, as well as the reference to the specific item.

If we want to add the value of the name field, we just have to define it accordingly:

```
    InfoFieldValue<String> nameInfoFieldValue = new InfoFieldValue<>(
        StarshipEntryInfoItemFields.nameInfoField, starshipEntry.getName());
```

This is the simplest example of retrieving the value of a field: we specify the name of the `InfoField` (which we created before) and provide a value.

But what happens if we have localizable text? Or an XML field containing the name for each available locale? We could do something like this:

```java
    InfoFieldValue<String> nameInfoFieldValue = new InfoFieldValue<>(
        StarshipEntryInfoItemFields.nameInfoField,
        InfoLocalizedValue.<String>builder(
        ).defaultLocale(
            LocaleUtil.fromLanguageId(starshipEntry.getDefaultLanguageId())
        ).values(
            starshipEntry.getNameMap()
        ).build());
```

In this case, we must specify a default locale for our translation, and a map where the appropriate value can be obtained depending on the context.

Another use case is when you have to do some transformation of the value you want to show. For this, `InfoFieldValue` can receive a function as a parameter. The function receives a locale, and you simply implement the transformation you need:

```java
    InfoFieldValue<String> nameInfoFieldValue = new InfoFieldValue<>(
        StarshipEntryInfoItemFields.nameInfoField,
        (InfoLocalizedValue<String>)InfoLocalizedValue.function(
            locale -> {
                return starshipEntry.getName(locale);
            }));
```


The `InfoItemFieldValues` builder also requires an `InfoItemReference`. This just defines the `className` and `classPK` of the values being retrieved. So the complete code would look something like this:

```java
@Component(service = InfoItemFieldValuesProvider.class)
public class StarshipEntryInfoItemFieldValuesProvider
    implements InfoItemFieldValuesProvider<StarshipEntry> {

    @Override
    public InfoItemFieldValues getInfoItemFieldValues(
        StarshipEntry starshipEntry) {

        return InfoItemFieldValues.builder(
        ).infoFieldValue(
            new InfoFieldValue<>(
                StarshipEntryInfoItemFields.nameInfoField,
                starshipEntry.getName())
        ).infoFieldValue(
            new InfoFieldValue<>(
                StarshipEntryInfoItemFields.descriptionInfoField,
                starshipEntry.getDescription())
        ).infoFieldValue(
            new InfoFieldValue<>(
                StarshipEntryInfoItemFields.createDateInfoField,
                starshipEntry.getCreateDate())
        ).infoItemReference(
            new InfoItemReference(
                StarshipEntry.class.getName(),
                starshipEntry.getStarshipEntryId())
        ).build();
    }
}
```

# Obtaining Our Object

We have described the fields that we want to display, and also the values that we want to display, but we haven't retrieved the specific object. The way to achieve this is to implement `InfoItemObjectProvider` with our `StarshipEntry` entity.

The method that we have to take into consideration is `public StarshipEntry getInfoItem(InfoItemIdentifier infoItemIdentifier)` which takes `InfoItemIdentifier` as parameter. There are three implementations of InfoItemIdentifier: 

* `ClassPKInfoItemIdentifier`: Used where we can identify an entity by its primary key
* `GroupUrlTitleInfoItemIdentifier`: Used in cases were we have a specific URL for a group
* `GroupKeyInfoItemIdentifier`: Used in cases were we have an external key for a group

In the case of mapping, we only have to take `ClassPKInfoItemIdentifier` into account. A more complex implementation would take all three cases into account when retrieving the entity.

```java
@Component(
    immediate = true, property = "service.ranking:Integer=100",
    service = InfoItemObjectProvider.class
)
public class StarshipEntryInfoItemObjectProvider
    implements InfoItemObjectProvider<StarshipEntry> {

    @Override
    public StarshipEntry getInfoItem(InfoItemIdentifier infoItemIdentifier)
        throws NoSuchInfoItemException {

        if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
            throw new NoSuchInfoItemException("Invalid infoItemIdentifier");
        }

        ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
            (ClassPKInfoItemIdentifier)infoItemIdentifier;

        StarshipEntry starshipEntry = _starshipEntryService.fetchStarshipEntry(
            classPKInfoItemIdentifier.getClassPK());

        if (starshipEntry == null) {
            throw new NoSuchInfoItemException("Invalid infoItemIdentifier");
        }

        return starshipEntry;
    }

    @Override
    public StarshipEntry getInfoItem(long classPK)
        throws NoSuchInfoItemException {

        ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
            new ClassPKInfoItemIdentifier(classPK);

        return getInfoItem(classPKInfoItemIdentifier);
    }

    private static final Log _log = LogFactoryUtil.getLog(
        StarshipEntryInfoItemObjectProvider.class);

    @Reference
    private StarshipEntryService _starshipEntryService;

}
```

We can now map fields to editable fields in pages once all of these classes have been implemented.