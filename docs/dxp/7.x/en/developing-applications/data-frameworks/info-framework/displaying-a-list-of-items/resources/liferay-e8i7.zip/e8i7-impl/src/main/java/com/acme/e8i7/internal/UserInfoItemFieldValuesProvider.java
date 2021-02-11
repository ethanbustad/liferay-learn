package com.acme.e8i7.internal;

import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.NumberInfoFieldType;
import com.liferay.info.field.type.TextInfoFieldType;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.portal.kernel.model.User;

import org.osgi.service.component.annotations.Component;

@Component(service = InfoItemFieldValuesProvider.class)
public class UserInfoItemFieldValuesProvider
	implements InfoItemFieldValuesProvider<User> {

	public static final InfoField<DateInfoFieldType> createDateInfoField =
		InfoField.builder(
		).infoFieldType(
			DateInfoFieldType.INSTANCE
		).name(
			"createDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(User.class, "date")
		).build();
	public static final InfoField<TextInfoFieldType> fullNameInfoField =
		InfoField.builder(
		).infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"fullName"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(User.class, "name")
		).build();
	public static final InfoField<TextInfoFieldType> modifiedDateInfoField =
		InfoField.builder(
		).infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"modifiedDate"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(User.class, "modified-date")
		).build();
	public static final InfoField<TextInfoFieldType> screenNameInfoField =
		InfoField.builder(
		).infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"screenName"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(User.class, "screen-name")
		).build();
	public static final InfoField<TextInfoFieldType> titleInfoField =
		InfoField.builder(
		).infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"title"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(User.class, "title")
		).build();

	// RIDICULOUS: FOLLOWING REQUIRED TO AVOID NPE ON VIEW ITEMS FUNCTION

	public static final InfoField<NumberInfoFieldType> userIdInfoField =
		InfoField.builder(
		).infoFieldType(
			NumberInfoFieldType.INSTANCE
		).name(
			"userId"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(User.class, "user-id")
		).build();
	public static final InfoField<TextInfoFieldType> userNameInfoField =
		InfoField.builder(
		).infoFieldType(
			TextInfoFieldType.INSTANCE
		).name(
			"userName"
		).labelInfoLocalizedValue(
			InfoLocalizedValue.localize(User.class, "user-name")
		).build();

	@Override
	public InfoItemFieldValues getInfoItemFieldValues(User user) {
		return InfoItemFieldValues.builder(
		).infoFieldValue(
			new InfoFieldValue<>(screenNameInfoField, user.getScreenName())
		).infoFieldValue(
			new InfoFieldValue<>(fullNameInfoField, user.getFullName())
		).infoFieldValue(
			new InfoFieldValue<>(createDateInfoField, user.getCreateDate())
		).infoFieldValue(
			new InfoFieldValue<>(userIdInfoField, user.getUserId())
		).infoFieldValue(
			new InfoFieldValue<>(modifiedDateInfoField, user.getModifiedDate())
		).infoFieldValue(
			new InfoFieldValue<>(titleInfoField, user.getFullName())
		).infoFieldValue(
			new InfoFieldValue<>(userNameInfoField, user.getFullName())
		).infoItemReference(
			new InfoItemReference(User.class.getName(), user.getUserId())
		).build();
	}

}