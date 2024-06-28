import android.os.Parcel
import android.os.Parcelable

data class Recipe(
    val id: String? = null,
    val title: String? = null,
    val thumbnailUrl: String? = null,
    val videoUrl: String? = null,
    var ingredients: List<String>? = null,
    val instructions: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(thumbnailUrl)
        parcel.writeString(videoUrl)
        parcel.writeStringList(ingredients)
        parcel.writeString(instructions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}



