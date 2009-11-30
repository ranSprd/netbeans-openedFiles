// created by : nagel, 01.08.2008
//
// function   : mark occurrences from selection
//              see http://blogs.sun.com/geertjan/entry/how_do_mark_occurrences_work
// todo       :
//
// modified   : 
package net.sf.multikulti.occurrences;

import javax.swing.text.Document;
import org.netbeans.spi.editor.highlighting.HighlightsLayer;
import org.netbeans.spi.editor.highlighting.HighlightsLayerFactory;
import org.netbeans.spi.editor.highlighting.ZOrder;

/**
 * 
 */
public class MarkTextOccurrencesFactory implements HighlightsLayerFactory
{

  public static MarkTextOccurrences getMarkOccurrencesHighlighter(Document doc)
  {
    MarkTextOccurrences highlighter = (MarkTextOccurrences) doc.getProperty(MarkTextOccurrences.class);
    if (highlighter == null)
    {
      doc.putProperty(MarkTextOccurrences.class, highlighter = new MarkTextOccurrences(doc));
    }
    return highlighter;
  }

  @Override
  public HighlightsLayer[] createLayers(Context context)
  {
    return new HighlightsLayer[]
      {
        HighlightsLayer.create(
        MarkTextOccurrences.class.getName(),
        ZOrder.CARET_RACK.forPosition(2000),
        true,
        getMarkOccurrencesHighlighter(context.getDocument()).getHighlightsBag())
      };
  }

}
