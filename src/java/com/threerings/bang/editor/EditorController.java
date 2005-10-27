//
// $Id$

package com.threerings.bang.editor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.samskivert.util.ListUtil;
import com.samskivert.util.StringUtil;

import com.threerings.util.MessageBundle;

import com.threerings.presents.dobj.EntryAddedEvent;
import com.threerings.presents.dobj.EntryRemovedEvent;
import com.threerings.presents.dobj.EntryUpdatedEvent;
import com.threerings.presents.dobj.SetListener;

import com.threerings.crowd.client.PlaceView;
import com.threerings.crowd.data.PlaceConfig;
import com.threerings.crowd.data.PlaceObject;
import com.threerings.crowd.util.CrowdContext;

import com.threerings.parlor.game.client.GameController;

import com.threerings.bang.game.data.BangObject;
import com.threerings.bang.game.data.PieceDSet;
import com.threerings.bang.game.data.piece.Marker;
import com.threerings.bang.game.data.piece.Piece;
import com.threerings.bang.server.persist.BoardRecord;

import static com.threerings.bang.Log.log;

/**
 * Handles the logic and flow for the Bang! board editor.
 */
public class EditorController extends GameController
{
    /** Requests that we terminate the editor. */
    public static final String EXIT = "Exit";

    /** Instructs us to create a piece of the supplied type. */
    public static final String CREATE_PIECE = "CreatePiece";

    /** Instructs us to remove the specified piece. */
    public static final String REMOVE_PIECE = "RemovePiece";

    /** Instructs us to load a new board. */
    public static final String LOAD_BOARD = "LoadBoard";

    /** Instructs us to save the current board. */
    public static final String SAVE_BOARD = "SaveBoard";

    /** Instructs us to import a heightfield. */
    public static final String IMPORT_HEIGHTFIELD = "ImportHeightfield";
    
    /** Instructs us to export a heightfield. */
    public static final String EXPORT_HEIGHTFIELD = "ExportHeightfield";
    
    /** Instructs us to bring up the water properties dialog. */
    public static final String EDIT_WATER = "EditWater";
    
    /** Instructs us to toggle the impassable tile highlights. */
    public static final String TOGGLE_HIGHLIGHTS = "ToggleHighlights";
    
    /** Instructs us to select one of the tools. */
    public static final String SELECT_TOOL = "SelectTool";
    
    /** Handles a request to exit the editor. Generated by the {@link
     * #EXIT} command. */
    public void handleExit (Object source)
    {
        // TODO: warn about an unsaved board
        System.exit(0);
    }

    /** Handles a request to create a new piece and add it to the board.
     * Generated by the {@link #CREATE_PIECE} command. */
    public void handleCreatePiece (Object source, Piece piece)
    {
        piece = (Piece)piece.clone();
        piece.assignPieceId();
        piece.position(0, 0);
        _bangobj.addToPieces(piece);
    }

    /** Handles a request to create a new piece and add it to the board.
     * Generated by the {@link #REMOVE_PIECE} command. */
    public void handleRemovePiece (Object source, Integer key)
    {
        _bangobj.removeFromPieces(key);
    }

    /** Handles a request to load the current board.  Generated by the
     * {@link #LOAD_BOARD} command. */
    public void handleLoadBoard (Object source)
    {
        if (_boardChooser == null) {
            _boardChooser = new JFileChooser(_board.getParent());
        }
        int rv = _boardChooser.showOpenDialog(_ctx.getFrame());
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }

        loadBoard(_boardChooser.getSelectedFile());
    }

    /** Handles a request to save the current board.  Generated by the
     * {@link #SAVE_BOARD} command. */
    public void handleSaveBoard (Object source)
    {
        if (_boardChooser == null) {
            _boardChooser = new JFileChooser(_board.getParent());
        }
        int rv = _boardChooser.showSaveDialog(_ctx.getFrame());
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            File board = _boardChooser.getSelectedFile();
            BoardRecord brec = new BoardRecord();
            _panel.info.toBoard(brec);
            brec.setData(_bangobj.board, _bangobj.getPieceArray());
            brec.save(board);
            _board = board;
            _ctx.setWindowTitle(_board.toString());
            _ctx.displayStatus(_msgs.get("m.saved", _board));

        } catch (IOException ioe) {
            _ctx.displayStatus(_msgs.get("m.save_error", ioe.getMessage()));
        }
    }

    /** Handles a request to import a heightfield.  Generated by the
     * {@link #IMPORT_HEIGHTFIELD} command. */
    public void handleImportHeightfield (Object source)
    {
        if (_imageChooser == null) {
            createImageChooser();
        }
        int rv = _imageChooser.showOpenDialog(_ctx.getFrame());
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        try {
            File heightfield = _imageChooser.getSelectedFile();
            _panel.view.setHeightfield(ImageIO.read(heightfield));
            _ctx.displayStatus(_msgs.get("m.imported", heightfield));
            
        } catch (IOException ioe) {
            _ctx.displayStatus(_msgs.get("m.import_error", ioe.getMessage()));
        }
    }
    
    /** Handles a request to export a heightfield.  Generated by the
     * {@link #EXPORT_HEIGHTFIELD} command. */
    public void handleExportHeightfield (Object source)
    {
        if (_imageChooser == null) {
            createImageChooser();
        }
        int rv = _imageChooser.showSaveDialog(_ctx.getFrame());
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        try {
            File heightfield = _imageChooser.getSelectedFile();
            String name = heightfield.getName(),
                suffix = name.substring(name.indexOf('.')+1);
            ImageIO.write(_panel.view.getHeightfieldImage(), suffix,
                heightfield);
            _ctx.displayStatus(_msgs.get("m.exported", heightfield));
            
        } catch (IOException ioe) {
            _ctx.displayStatus(_msgs.get("m.export_error", ioe.getMessage()));
        }
    }
    
    /** Handles a request to edit the water properties.  Generated by the
     * {@link #EDIT_WATER} command. */
    public void handleEditWater (Object source)
    {
        if (_water == null) {
            _water = new WaterDialog(_ctx, _panel);
        }
        _water.fromBoard(_bangobj.board);
        _water.setLocation(100, 100);
        _water.setLocationRelativeTo(_ctx.getFrame());
        _water.setVisible(true);
    }
    
    /** Handles a request to toggle the highlights.  Generated by the
     * {@link #TOGGLE_HIGHLIGHTS} command. */
    public void handleToggleHighlights (Object source)
    {
        _panel.view.toggleHighlights();
    }
    
    /** Handles a request to select a tool.  Generated by the
     * {@link #SELECT_TOOL} command. */
    public void handleSelectTool (Object source, String name)
    {
        _panel.tools.selectTool(name);
    }
    
    // documentation inherited
    public void init (CrowdContext ctx, PlaceConfig config)
    {
        super.init(ctx, config);
        _ctx = (EditorContext)ctx;
        _config = (EditorConfig)config;
        _msgs = _ctx.getMessageManager().getBundle("editor");
    }

    // documentation inherited
    public void willEnterPlace (PlaceObject plobj)
    {
        super.willEnterPlace(plobj);
        _bangobj = (BangObject)plobj;
        _bangobj.addListener(_pclistener);
    }

    protected void loadBoard (File board)
    {
        try {
            BoardRecord brec = new BoardRecord();
            brec.load(board);
            _bangobj.setBoard(brec.getBoard());
            Piece[] pieces = brec.getPieces();
            // reassign piece ids
            for (int ii = 0; ii < pieces.length; ii++) {
                pieces[ii].pieceId = (ii+1);
            }
            Piece.setNextPieceId(pieces.length);
            _bangobj.setPieces(new PieceDSet(pieces));
            _panel.view.refreshBoard();
            _panel.info.fromBoard(brec);
            updatePlayerCount();
            _board = board;
            _ctx.setWindowTitle(_board.toString());
            _ctx.displayStatus(_msgs.get("m.loaded", _board));

        } catch (IOException ioe) {
            _ctx.displayStatus(_msgs.get("m.load_error", ioe.getMessage()));
        }
    }

    /**
     * Creates and initializes the image file chooser.
     */
    protected void createImageChooser ()
    {
        _imageChooser = new JFileChooser(_board.getParent());
        _imageChooser.setFileFilter(new FileFilter() {
            public boolean accept (File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String name = f.getName(),
                    suffix = name.substring(name.lastIndexOf('.')+1);
                return ListUtil.contains(ImageIO.getReaderFormatNames(),
                    suffix) &&
                    ListUtil.contains(ImageIO.getWriterFormatNames(), suffix);
            }
            public String getDescription () {
                return _msgs.get("m.hf_images");
            }
        });
    }
    
    @Override // documentation inherited
    protected PlaceView createPlaceView (CrowdContext ctx)
    {
        _panel = new EditorPanel((EditorContext)ctx, this);
        return _panel;
    }

    @Override // documentation inherited
    protected void gameDidStart ()
    {
        super.gameDidStart();

        // our panel needs to do some game starting business
        _panel.startGame(_bangobj, _config);

        // load up any board specified on the command line
        if (EditorApp.appArgs.length > 0 &&
            !StringUtil.blank(EditorApp.appArgs[0])) {
            loadBoard(new File(EditorApp.appArgs[0]));
        }
    }

    @Override // documentation inherited
    protected void gameWillReset ()
    {
        super.gameWillReset();
        _panel.endGame();
    }

    @Override // documentation inherited
    protected void gameDidEnd ()
    {
        super.gameDidEnd();
        _panel.endGame();
    }

    protected void updatePlayerCount ()
    {
        int pcount = 0;
        for (Iterator iter = _bangobj.pieces.iterator(); iter.hasNext(); ) {
            if (Marker.isMarker((Piece)iter.next(), Marker.START)) {
                pcount++;
            }
        }
        _panel.info.updatePlayers(pcount);
    }

    /** Listens for piece additions and removals. */
    protected SetListener _pclistener = new SetListener() {
        public void entryAdded (EntryAddedEvent event) {
            updatePlayerCount();
        }
        public void entryUpdated (EntryUpdatedEvent event) {
        }
        public void entryRemoved (EntryRemovedEvent event) {
            updatePlayerCount();
        }
    };

    /** A casted reference to our context. */
    protected EditorContext _ctx;

    /** The configuration of this game. */
    protected EditorConfig _config;

    /** Used to translate messages. */
    protected MessageBundle _msgs;

    /** Contains our main user interface. */
    protected EditorPanel _panel;

    /** A casted reference to our game object. */
    protected BangObject _bangobj;

    /** The file chooser we use for loading and saving boards. */
    protected JFileChooser _boardChooser;

    /** The file chooser we use for importing and exporting images. */
    protected JFileChooser _imageChooser;
    
    /** The water properties dialog. */
    protected WaterDialog _water;
    
    /** A reference to the file associated with the board we're editing. */
    protected File _board = new File("");
}
