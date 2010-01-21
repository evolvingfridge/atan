package atan.model;

//~--- non-JDK imports --------------------------------------------------------

import atan.model.enums.PlayMode;

import atan.parser.CommandFilter;
import atan.parser.Filter;
import atan.parser.trainer.CmdParserTrainer;

import org.apache.log4j.Logger;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;
import java.io.StringReader;

/**
 * A simple implementation of AbstractUDPClient for Trainers.
 * @author Atan
 */
public class SServerTrainer extends AbstractUDPClient implements ActionsTrainer {
    private static Logger              log            = Logger.getLogger(SServerTrainer.class);
    private String                     initMessage    = null;
    private final CmdParserTrainer     parser         = new CmdParserTrainer(new StringReader(""));
    private final Filter               filter         = new Filter();
    private final CommandFactory       commandFactory = new CommandFactory();
    private final SServerCommandBuffer cmdBuf         = new SServerCommandBuffer();
    private ControllerTrainer          controller;
    private String                     teamName;

    /**
     * A part constructor for SServerTrainer (assumes localhost:6001)
     * @param teamName The team name.
     * @param t
     */
    public SServerTrainer(String teamName, ControllerTrainer t) {
        this(teamName, t, 6001, "localhost");
    }

    /**
     * The full constructor for SServerCoach
     * @param teamName The teams name.
     * @param t
     * @param port The port to connect to.
     * @param hostname The host address.
     */
    public SServerTrainer(String teamName, ControllerTrainer t, int port, String hostname) {
        super(port, hostname);
        this.teamName   = teamName;
        this.controller = t;
    }

    /**
     * Gets the initialisation message.
     * @return Initialisation message.
     */
    @Override
    public String getInitMessage() {
        return initMessage;
    }

    /**
     * Connects to the server via AbstractUDPClient.
     */
    public void connect() {
        CommandFactory f = new CommandFactory();
        f.addTrainerInitCommand("");
        initMessage = f.next();
        super.start();
    }

    /**
     *
     * @param msg
     * @throws IOException
     */
    @Override
    public void received(String msg) throws IOException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("<---'" + msg + "'");
            }
            filter.run(msg, cmdBuf);
            cmdBuf.takeStep(controller, parser, this);
            while (commandFactory.hasNext()) {
                String cmd = commandFactory.next();
                if (log.isDebugEnabled()) {
                    log.debug("--->'" + cmd + "'");
                }
                send(cmd);
                pause(50);
            }
        } catch (Exception ex) {
            log.error("Error while receiving message: " + msg + " " + ex.getMessage(), ex);
        }
    }

    /**
     * Changes the play mode of the game.
     * @param playMode A valid play mode. (ie. the enum(
     */
    @Override
    public void changePlayMode(PlayMode playMode) {
        this.commandFactory.addChangePlayModeCommand(playMode);
    }

    /**
     * Moves the given player to the given coordinates.
     * @param p The player to move.
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    @Override
    public void movePlayer(ActionsPlayer p, double x, double y) {
        this.commandFactory.addMovePlayerCommand(p, x, y);
    }

    /**
     * Moves the ball to the given coordinates.
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    @Override
    public void moveBall(double x, double y) {
        this.commandFactory.addMoveBallCommand(x, y);
    }

    /**
     *
     */
    @Override
    public void checkBall() {
        this.commandFactory.addCheckBallCommand();
    }

    /**
     *
     */
    @Override
    public void startGame() {
        this.commandFactory.addStartCommand();
    }

    /**
     *
     */
    @Override
    public void recover() {
        this.commandFactory.addRecoverCommand();
    }

    /**
     *
     */
    @Override
    public void ear() {
        this.commandFactory.addEarCommand();
    }

    /**
     *
     */
    @Override
    public void eye() {
        this.commandFactory.addEyeCommand();
    }

    /**
     *
     */
    @Override
    public void look() {
        this.commandFactory.addLookCommand();
    }

    /**
     *
     */
    @Override
    public void teamNames() {
        this.commandFactory.addTeamNamesCommand();
    }

    /**
     *
     * @param teamName
     * @param unum
     * @param playerType
     */
    @Override
    public void changePlayerType(String teamName, Object unum, Object playerType) {
        this.commandFactory.addChangePlayerTypeCommand(teamName, unum, playerType);
    }

    /**
     *
     * @param message
     */
    @Override
    public void say(String message) {
        this.commandFactory.addSayCommand(message);
    }

    /**
     *
     */
    @Override
    public void bye() {
        this.commandFactory.addByeCommand();
    }

    /**
     *
     * @param ms
     */
    private synchronized void pause(int ms) {
        try {
            this.wait(ms);
        } catch (InterruptedException ex) {}
    }

    /**
     *
     * @param error
     */
    @Override
    public void handleError(String error) {
        log.error(error);
    }

    /**
     * A private controller-style filter
     * @author Atan
     */
    private class SServerCommandBuffer implements CommandFilter {
        private String changePlayerTypeCommand = null;
        private String errorCommand            = null;
        private String hearCommand             = null;
        private String initCommand             = null;
        private String okCommand               = null;
        private String playerParamCommand      = null;
        private String playerTypeCommand       = null;
        private String seeCommand              = null;
        private String senseBodyCommand        = null;
        private String serverParamCommand      = null;
        private String warningCommand          = null;

        /**
         *
         * @param cmd
         */
        @Override
        public void seeCommand(String cmd) {
            seeCommand = cmd;
        }

        /**
         *
         * @param cmd
         */
        @Override
        public void hearCommand(String cmd) {
            hearCommand = cmd;
        }

        /**
         *
         * @param cmd
         */
        @Override
        public void senseBodyCommand(String cmd) {
            senseBodyCommand = cmd;
        }

        /**
         *
         * @param cmd
         */
        @Override
        public void initCommand(String cmd) {
            initCommand = cmd;
        }

        /**
         *
         * @param cmd
         */
        @Override
        public void errorCommand(String cmd) {
            errorCommand = cmd;
        }

        /**
         *
         * @param cmd
         */
        @Override
        public void serverParamCommand(String cmd) {
            serverParamCommand = cmd;
        }

        /**
         *
         * @param cmd
         */
        @Override
        public void playerParamCommand(String cmd) {
            playerParamCommand = cmd;
        }

        /**
         *
         * @param cmd
         */
        @Override
        public void playerTypeCommand(String cmd) {
            playerTypeCommand = cmd;
        }

        /**
         *
         * @param cmd
         */
        @Override
        public void changePlayerTypeCommand(String cmd) {
            changePlayerTypeCommand = cmd;
        }

        /**
         *
         * @param cmd
         */
        @Override
        public void okCommand(String cmd) {
            okCommand = cmd;
        }

        /**
         *
         * @param cmd
         */
        @Override
        public void warningCommand(String cmd) {
            warningCommand = cmd;
        }

        /**
         *
         * @param controller
         * @param parser
         * @param c
         * @throws Exception
         */
        public void takeStep(ControllerTrainer controller, CmdParserTrainer parser, ActionsTrainer c) throws Exception {
            if (seeCommand != null) {
                parser.parseSeeCommand(seeCommand, controller, c);
                seeCommand = null;
            }
            if (hearCommand != null) {
                parser.parseHearCommand(hearCommand, controller, c);
                hearCommand = null;
            }
            if (senseBodyCommand != null) {
                parser.parseSenseBodyCommand(senseBodyCommand, controller, c);
                senseBodyCommand = null;
            }
            if (initCommand != null) {
                parser.parseInitCommand(initCommand, controller, c);
                initCommand = null;
            }
            if (okCommand != null) {
                parser.parseOkCommand(okCommand, controller, c);
                okCommand = null;
            }
            if (warningCommand != null) {
                parser.parseWarningCommand(warningCommand, controller, c);
                warningCommand = null;
            }
            if (serverParamCommand != null) {
                parser.parseServerParamCommand(serverParamCommand, controller, c);
                serverParamCommand = null;
            }
            if (playerParamCommand != null) {
                parser.parsePlayerParamCommand(playerParamCommand, controller, c);
                playerParamCommand = null;
            }
            if (playerTypeCommand != null) {
                parser.parsePlayerTypeCommand(playerTypeCommand, controller, c);
                playerTypeCommand = null;
            }
            if (changePlayerTypeCommand != null) {
                parser.parseChangePlayerTypeCommand(changePlayerTypeCommand, controller, c);
                changePlayerTypeCommand = null;
            }
            if (errorCommand != null) {
                parser.parseErrorCommand(errorCommand, controller, c);
                errorCommand = null;
            }
        }
    }
}
