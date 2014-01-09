package org.melato.android.bookmark;

import java.util.ArrayList;
import java.util.List;

import org.melato.bus.android.R;
import org.melato.client.Bookmark;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class BookmarksActivity extends ListActivity {
  private BookmarkHandler bookmarkHandler;
  List<Bookmark> bookmarks;
  
  public BookmarksActivity(BookmarkHandler bookmarkHandler) {
    super();
    this.bookmarkHandler = bookmarkHandler;
  }

  class LoadTask extends AsyncTask<Void,Void,List<Bookmark>> {

    @Override
    protected List<Bookmark> doInBackground(Void... args) {
      List<Bookmark> bookmarks = new ArrayList<Bookmark>();
      BookmarkDatabase db = BookmarkDatabase.getInstance(BookmarksActivity.this);
      db.loadBookmarks(bookmarks);
      return bookmarks;
    }

    @Override
    protected void onPostExecute(List<Bookmark> result) {
      bookmarks = result;
      setListAdapter(new BookmarkAdapter());
      //setListAdapter(new ArrayAdapter<Bookmark>(BookmarksActivity.this, R.layout.list_item, bookmarks));
      super.onPostExecute(result);
    }    
  }
  
  class BookmarkAdapter extends ArrayAdapter<Bookmark> {
    public BookmarkAdapter() {
      super(BookmarksActivity.this, R.layout.bookmark_list_item, R.id.text, bookmarks); 
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = super.getView(position, convertView, parent);
      TextView textView = (TextView) view.findViewById(R.id.text);
      ImageView iconView = (ImageView) view.findViewById(R.id.icon);
      Bookmark bookmark = bookmarks.get(position);
      textView.setText(bookmark.getName());
      iconView.setImageResource(bookmarkHandler.getTypeIcon(bookmark.getType()));
      return view;
    }
  }
  
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Bookmark bookmark = bookmarks.get(position);
    bookmarkHandler.open(this, bookmark);    
  }

  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    new LoadTask().execute();      
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

}
