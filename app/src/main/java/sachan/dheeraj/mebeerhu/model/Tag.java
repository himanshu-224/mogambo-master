package sachan.dheeraj.mebeerhu.model;

/**
 * Created by naveen.goel on 12/07/15.
 */
public class Tag {
    private String tagName;
    private String tagMeaning;
    private int        typeId;
    private boolean     approved;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagMeaning() {
        return tagMeaning;
    }

    public void setTagMeaning(String tagMeaning) {
        this.tagMeaning = tagMeaning;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public boolean equals(Object o) {
        Tag tag = (Tag) o;
        return tag.tagName.equals(tagName);
    }

    @Override
    public int hashCode() {
        return tagName.hashCode();
    }
}
