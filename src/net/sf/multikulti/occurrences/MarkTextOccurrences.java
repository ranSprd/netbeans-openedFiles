// created by : nagel, 01.08.2008
//
// function   : 
//
// todo       : find is based on regular expressions, 
//
// modified   : 
package net.sf.multikulti.occurrences;

import java.awt.Color;
import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JEditorPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;
import org.netbeans.api.editor.settings.AttributesUtilities;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.spi.editor.highlighting.support.OffsetsBag;
import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.loaders.DataObject;
import org.openide.util.RequestProcessor;
/**
 * 
 */
public class MarkTextOccurrences implements CaretListener
{

  private static final AttributeSet defaultColors =
          AttributesUtilities.createImmutable(StyleConstants.Background,
                                              new Color(236, 235, 163));
  private final OffsetsBag bag;
  private JTextComponent comp;
  private final WeakReference weakDoc;

  public MarkTextOccurrences(Document doc)
  {
    bag = new OffsetsBag(doc);
    weakDoc = new WeakReference<Document>(doc);
    DataObject dobj = NbEditorUtilities.getDataObject((Document) weakDoc.get() );
    if (dobj != null)
    {
      EditorCookie pane = dobj.getCookie(EditorCookie.class);
      JEditorPane[] panes = pane.getOpenedPanes();
      if (panes != null && panes.length > 0)
      {
        comp = panes[0];
        comp.addCaretListener(this);
      }
    }
  }

  @Override
  public void caretUpdate(CaretEvent e)
  {
    // is there a selection?
    int diff = e.getDot() - e.getMark() ;
    if (diff != 0)
    {
      // delete a prev highlighting
      bag.clear();

      diff = Math.abs(diff) ;
      if (diff > 3)
      {
        scheduleUpdate();
      }
    }
  }
  private RequestProcessor.Task task = null;
  private final static int DELAY = 100;

  public void scheduleUpdate()
  {
    if (task == null)
    {
      task = RequestProcessor.getDefault().create(new Runnable()
      {

        public void run()
        {
          String selection = comp.getSelectedText();
          if (selection != null)
          {
            if (selection.length() > 3)
            {
              selection = selection.replaceAll("\\[", " ") ;
              selection = selection.replaceAll("\\]", " ") ;
              try
              {
                Pattern p = Pattern.compile(selection);
                Matcher m = p.matcher(comp.getText());
                while (m.find() == true)
                {
                  int startOffset = m.start();
                  int endOffset = m.end();
                  bag.addHighlight(startOffset, endOffset, defaultColors);
                }
              }
              catch (Exception ex)
              {
                ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, ex.getMessage()) ;
              }
            }
          }
        }
      }, true);
      task.setPriority(Thread.MIN_PRIORITY);
    }
    task.cancel();
    task.schedule(DELAY);
  }

  public OffsetsBag getHighlightsBag()
  {
    return bag;
  }
}
