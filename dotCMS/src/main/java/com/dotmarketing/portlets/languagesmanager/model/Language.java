package com.dotmarketing.portlets.languagesmanager.model;

import static com.dotcms.util.CollectionsUtils.map;

import com.dotcms.publisher.util.PusheableAsset;
import com.dotcms.publishing.manifest.ManifestItem;
import com.dotcms.publishing.manifest.ManifestItem.ManifestInfo;
import java.io.Serializable;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.dotmarketing.exception.DotRuntimeException;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 *
 * @author  maria
 */
public class Language implements Serializable, ManifestItem {
    private static final long serialVersionUID = 1L;

    /** identifier field */
    private long id;

    /** identifier field */
    private String languageCode;

    /** identifier field */
    private String countryCode;

    /** identifier field */
    private String language;

    /** nullable persistent field */
    private String country;

    private String isoCode;

    /**
     * @param languageCode
     * @param countryCode
     * @param language
     * @param country
     */
    public Language(long id, String languageCode, String countryCode, String language, String country) {
        super();
        this.id = id;
        this.languageCode = languageCode;
        this.countryCode = countryCode;
        this.language = language;
        this.country = country;
    }

    public Language() {
        super();
        this.id = 0;
        this.languageCode = "";
        this.countryCode = "";
        this.language = "";
        this.country = "";
    }

    public Language(long id) {
        super();
        this.id = id;
        this.languageCode = "";
        this.countryCode = "";
        this.language = "";
        this.country = "";
    }

    /**
     * @return Returns the serialVersionUID.
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return country;
    }

    @JsonIgnore
    public Locale asLocale() {
      if(this.languageCode==null) {
        throw new DotRuntimeException("Locale requires a language code");
      }
      else if(this.countryCode==null) {
        return new Locale(this.languageCode);
      }else {
        return new Locale(this.languageCode, this.countryCode);
      }
    }
    
    
    
    /**
     * @param country The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return Returns the countryCode.
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * @param countryCode The countryCode to set.
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * @return Returns the language.
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language The language to set.
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return Returns the languageCode.
     */
    public String getLanguageCode() {
        return languageCode;
    }

    /**
     * @param languageCode The languageCode to set.
     */
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }

    /**
     * @return Returns the id.
     */
    public long getId() {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(long id) {
        this.id = id;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Language)) {
            return false;
        }

        Language castOther = (Language) other;

        return new EqualsBuilder().append(this.id, castOther.id).isEquals();
    }

    public String getIsoCode() {
        if (isoCode == null) {
            isoCode = ((StringUtils.isNotBlank(countryCode)) ? languageCode.toLowerCase() + "-"
                    + countryCode : languageCode).toLowerCase();
        }
        return isoCode;
    }

	@Override
	public String toString() {
		return getIsoCode();
	}

    @JsonIgnore
    @Override
    public ManifestInfo getManifestInfo(){
        return new ManifestInfoBuilder()
                .objectType(PusheableAsset.LANGUAGE.getType())
                .id(String.valueOf(this.id))
                .title(this.language)
                .build();
    }



}
