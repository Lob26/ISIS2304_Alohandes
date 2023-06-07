package edu.uniandes.view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import edu.uniandes.UIConfig;
import edu.uniandes.business.AlohandesB;
import edu.uniandes.data.*;
import edu.uniandes.exception.UserException;
import edu.uniandes.util.OrderedMap;
import edu.uniandes.util.TextTable;
import edu.uniandes.view.input.DatePicker;
import edu.uniandes.view.input.TimePicker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@SuppressWarnings({"unused", "FeatureEnvy"}) public final class AlohandesV extends JFrame implements ActionListener {
    private final static Supplier<JComboBox<String>> YES_NO = () -> new JComboBox<>(new String[]{"Si", "No"});
    private final static Supplier<TimePicker> TIME = TimePicker::new;
    private final static Supplier<DatePicker> DATE = DatePicker::new;
    private final static Logger LOG = LogManager.getLogger(AlohandesV.class);
    private final static int FIELD_SIZE = 20;
    private final static Supplier<JTextField> TEXT_FIELD = () -> new JTextField(FIELD_SIZE);
    private final static Supplier<JTextField> NUMERIC_FIELD = () -> new JTextField(
            new javax.swing.text.PlainDocument() {
                @Override public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
                        throws javax.swing.text.BadLocationException {
                    if (str.matches("\\d+")) super.insertString(offs, str, a);
                }
            }, null, FIELD_SIZE);
    private final static Supplier<JComboBox<String>> OPERATOR_BOX = () -> new JComboBox<>(
            new String[]{"Apartamento", "Habitacion de Hotel", "Habitacion de casa", "Hostal",
                    "Residencia estudiantil", "Residencia no-estudiantil"});
    private static boolean DARK;
    private final Console console = new Console();
    private final Cmd cmd = new Cmd(this);
    private final AlohandesB business;

    public AlohandesV(AlohandesB business)
            throws HeadlessException {
        super("AlohandesB");
        setLayout(new BorderLayout());
        this.business = business;
    }

    private static String generateErrorMessage(Throwable e) {
        String clName, msg, title;
        if (e instanceof RuntimeException re) {
            try {
                clName = re.getCause().getClass().getSimpleName();
                msg = re.getCause().getMessage();
            } catch (NullPointerException npe) {
                clName = re.getClass().getSimpleName();
                msg = re.getMessage();
            }

        } else {
            clName = e.getClass().getSimpleName();
            msg = e.getMessage();
        }
        title = switch (clName) {
            case "IndexOutOfBoundsException" -> "No hay elementos";
            case "NullPointerException" -> "Inexistente";
            case "AssertionError" -> "User error";
            case "JDODataStoreException" -> "Error con SQL";
            default -> clName;
        };
        return "=-------------------" + title + "--------------------=\n"
               + msg.trim() + "\n" +
               "=-------------------" + title + "--------------------=\n";
    }

    //<editor-fold desc="PK_Get">
    private static Object[] pkApartment(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.append("ID del apartamento", NUMERIC_FIELD.get());
        int selected = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput().getValues();
    }

    private static Object[] pkBooking(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.append("ID de la reserva", NUMERIC_FIELD.get());
        int selected = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput().getValues();
    }

    private static Object[] pkClient(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.append("NUIP del cliente", NUMERIC_FIELD.get());
        int selected = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput().getValues();
    }

    private static Object[] pkHostel(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.append("ID del hostal", NUMERIC_FIELD.get());
        int selected = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput().getValues();
    }

    private static Object[] pkHotel(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.append("NIT del hotel", NUMERIC_FIELD.get());
        int selected = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput().getValues();
    }

    private static Object[] pkHotelRoom(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.append("ID de la habitacion de hotel", NUMERIC_FIELD.get());
        int selected = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput().getValues();
    }

    private static Object[] pkHouseRoom(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.append("ID de la habitacion de casa", NUMERIC_FIELD.get());
        int selected = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput().getValues();
    }

    private static Object[] pkOffer(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.append("ID de la oferta", NUMERIC_FIELD.get());
        int selected = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput().getValues();
    }

    private static Object[] pkGroupReservation(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.append("ID de la reserva grupal", NUMERIC_FIELD.get());
        int selected = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput().getValues();
    }

    private static Object[] pkResidence(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.append("ID de la residencia no-estudiantil", NUMERIC_FIELD.get());
        int selected = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput().getValues();
    }

    private static Object[] pkRoomsHotel(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.append("NIT del hotel", NUMERIC_FIELD.get()).append("ID de la habitacion de hotel", NUMERIC_FIELD.get());
        int selected = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput().getValues();
    }

    private static Object[] pkStudentRes(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.append("ID de la residencia estudiantil", NUMERIC_FIELD.get());
        int selected = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput().getValues();
    }
    //</editor-fold>

    public void config(URL menu1URL, URL menu2URL)
            throws IOException {
        InputStream menu1 = menu1URL.openStream();
        InputStream menu2 = menu2URL.openStream();
        Yaml[] menus = {new Yaml(), new Yaml()};

        JMenu reqs = new JMenu("Requerimientos");
        var load = menus[0].<Map<String, Map<String, String>>>load(menu1);
        load.get("Funcional").forEach((k, v) -> reqs.add(
                new JMenuItem() {{
                    addActionListener(AlohandesV.this);
                    setText(k);
                    setActionCommand(v);
                }}));
        load.get("Consulta").forEach((k, v) -> reqs.add(
                new JMenuItem() {{
                    addActionListener(AlohandesV.this);
                    setText(k);
                    setActionCommand(v);
                }}));

        JMenu util = new JMenu("Utility");
        menus[1].<Map<String, Map<String, String>>>load(menu2)
                .forEach((String k, Map<String, String> v) -> util.add(
                        new JMenu(k) {{
                            v.forEach((String k1, String v1) -> add(
                                    new JMenuItem() {{
                                        addActionListener(AlohandesV.this);
                                        setText(k1);
                                        setActionCommand(v1);
                                    }}));
                        }}));
        setJMenuBar(new JMenuBar() {{
            add(reqs);
            add(util);
        }});
    }

    public void config(URL appURL)
            throws IOException {
        InputStream app = appURL.openStream();
        Yaml yaml = new Yaml();
        Map<String, Object> load = yaml.load(app);
        var body = new Object() {
            private final String title = String.valueOf(load.get("title"));
            private final int frameW = (Integer) load.get("frameW");
            private final int frameH = (Integer) load.get("frameH");
            private final String banner = "/" + load.get("banner");
        };
        setTitle(body.title);
        setSize(body.frameW, body.frameH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JLabel jLabel = new JLabel(
                new ImageIcon(Objects.requireNonNull(getClass().getResource(body.banner)).getPath()));
        jLabel.setHorizontalAlignment(2);
        add(jLabel,
            BorderLayout.NORTH);
        add(console, BorderLayout.CENTER);
        add(cmd, BorderLayout.SOUTH);
    }

    @Override public void actionPerformed(ActionEvent e) {
        String event = e.getActionCommand();
        try {AlohandesV.class.getDeclaredMethod(event).invoke(this);}
        catch (Exception ex) {throw new RuntimeException(event + "\n", ex);}
    }

    //<editor-fold desc="One_Level">
    void req01() {createOperator();}

    void req02() {createBooking();}

    void req03() {createClient();}

    void req04() {createOffer();}

    void req05() {deleteOffer();}

    void req06() {deleteBooking();}

    void req07() {createGroupReservation();}

    void req08() {deleteGroupReservation();}

    void req09() {
        catcher(() -> new LinkedList<>() {{
            add(false);
            addAll(Arrays.asList(pkBooking("changeStatusBooking")));
        }}.toArray(), business::changeStatusBooking);
    }

    void req10() {catcher(() -> new LinkedList<>() {{
        add(true);
        addAll(Arrays.asList(pkBooking("changeStatusBooking")));
    }}.toArray(), business::changeStatusBooking);
    }

    void reqC01() {simpleCatcher(business::moneyByYear);}

    void reqC02() {simpleCatcher(business::popularOffers);}

    void reqC03() {simpleCatcher(business::occupationIndex);}

    void reqC04() {
        catcher(() -> {
            JTextArea area = new JTextArea(10, FIELD_SIZE);
            area.setLineWrap(true);
            area.setPreferredSize(new Dimension(area.getRows(), area.getColumns()));


            InputBuilder ib = new InputBuilder();
            ib.appends(new String[]{"Inicio del rango", "Fin del rango"}, new Component[]{DATE.get(), DATE.get()})
              .append("Servicios (separados por ,)", area);
            int selected = JOptionPane.showConfirmDialog(null, ib.build(), "getBookingsRange",
                                                         JOptionPane.OK_CANCEL_OPTION);
            if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
            return ib.getInput().getValues();
        }, business::availableInRangeAndServices);
    }

    void reqC05() {simpleCatcher(business::alohandesUse);}

    void reqC06() {
        catcher(() -> {
            InputBuilder ib = new InputBuilder();
            ib.append("NUIP del usuario", NUMERIC_FIELD.get());
            int selected = JOptionPane.showConfirmDialog(null, ib.build(), "getAlohandesUse",
                                                         JOptionPane.OK_CANCEL_OPTION);
            if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
            return ib.getInput().getValues();
        }, business::alohandesUse);
    }

    void reqC07() {
        catcher(() -> {
            InputBuilder ib = new InputBuilder();
            ib.append("Unidad de tiempo", new JComboBox<>(
                      new String[]{"Dia", "Semana", "Mes", "Anio"}))
              .append("Tipo de operador", OPERATOR_BOX.get());
            int selected = JOptionPane.showConfirmDialog(null, ib.build(), "alohandesOperation",
                                                         JOptionPane.OK_CANCEL_OPTION);
            if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
            return ib.getInput().getValues();
        }, business::alohandesOperation);
    }

    void reqC08() {
        catcher(() -> {
            InputBuilder ib = new InputBuilder();
            ib.append("Nombre del operador", TEXT_FIELD.get());
            int selected = JOptionPane.showConfirmDialog(null, ib.build(), "frequentClients",
                                                         JOptionPane.OK_CANCEL_OPTION);
            if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
            return ib.getInput().getValues();
        }, business::frequentClients);
    }

    void reqC09() {simpleCatcher(business::lowDemand);}

    //</editor-fold>

    //<editor-fold desc="C.Two_Level">
    void createOperator() {
        String methodName = "createOperator";
        simpleCatcher(() -> {
            OrderedMap<String, String> iOperator = createOperatorFirstInput(methodName);
            OrderedMap<String, String> iOperatorSpec = createOperatorSecondInput(methodName);
            OrderedMap<String, String> iOperatorService = createOperatorThirdInput(methodName);
            String s = iOperator.get("Que tipo de operador es?");
            OrderedMap<String, String> iInheritor = createOperatorFourthInput(s, methodName);
            Set<OrderedMap<String, String>> iService = "Habitacion de casa".equals(s)
                    ? Collections.emptySet()
                    : createServices();
            return business.createOperator(iOperator, iOperatorSpec, iOperatorService, iInheritor, iService);
        });
    }

    private OrderedMap<String, String> createOperatorFirstInput(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.append("NUIP del operador", NUMERIC_FIELD.get()).append("Nombre del operador", TEXT_FIELD.get())
          .append("Que tipo de operador es?", OPERATOR_BOX.get());
        int first = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (first == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput();
    }

    private OrderedMap<String, String> createOperatorSecondInput(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.appends(new String[]{"Capacidad", "Tamanio", "Localizacion", "Compartido?"},
                   new Component[]{NUMERIC_FIELD.get(), NUMERIC_FIELD.get(), TEXT_FIELD.get(), YES_NO.get()});
        int second = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (second == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput();
    }

    private OrderedMap<String, String> createOperatorThirdInput(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.appends(new String[]{"Amueblado?", "Tiene wifi?", "Tiene cocineta?"},
                   new Component[]{YES_NO.get(), YES_NO.get(), YES_NO.get()});
        int third = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (third == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput();
    }

    private OrderedMap<String, String> createOperatorFourthInput(String type,
                                                                 String methodName) {
        InputBuilder ib = new InputBuilder();
        switch (type) {
            case "Residencia estudiantil" ->
                    ib.appends(new String[]{"Coste del restaurante", "Coste de la sala de estudio"},
                               new Component[]{NUMERIC_FIELD.get(), NUMERIC_FIELD.get()})
                      .appends(new String[]{"Coste por sala de ocio", "Coste por gimnasio"},
                               new Component[]{NUMERIC_FIELD.get(), NUMERIC_FIELD.get()});
            case "Residencia no-estudiantil" ->
                    ib.appends(new String[]{"Numero de habitaciones", "Cuota administrativa", "Descripcion del seguro"},
                               new Component[]{NUMERIC_FIELD.get(), NUMERIC_FIELD.get(), TEXT_FIELD.get()});
            case "Hostal" -> ib.append("NIT", NUMERIC_FIELD.get())
                               .appends(new String[]{"Horario de apertura", "Horario de cierre"},
                                        new Component[]{TIME.get(), TIME.get()});
            case "Habitacion de Hotel" ->
                    ib.append("NIT del hotel", NUMERIC_FIELD.get()).append("Numero de habitacion", NUMERIC_FIELD.get())
                      .append("Tipo de sala", new JComboBox<>(new String[]{"Estandar", "Semi-suite", "Suite"}))
                      .appends(new String[]{"Tiene baniera?", "Tiene yacuzzi?", "Tiene sala?"},
                               new Component[]{YES_NO.get(), YES_NO.get(), YES_NO.get()});
            case "Habitacion de casa" -> ib.appends(new String[]{"Tiene comidas incluidas?", "Tiene banio privado?"},
                                                    new Component[]{YES_NO.get(), YES_NO.get()});
            case "Apartamento" -> ib.append("Cuota administrativa", NUMERIC_FIELD.get())
                                    .appends(new String[]{"Servicios incluidos?", "Television incluida?"},
                                             new Component[]{YES_NO.get(), YES_NO.get()});
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
        int fourth = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (fourth == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput();
    }

    void createClient() {
        String methodName = "createClient";
        simpleCatcher(() -> {
            OrderedMap<String, String> iClient = createClientFirstInput(methodName);
            OrderedMap<String, String> iClientPreference = createClientSecondInput(methodName);
            return business.createClient(iClient, iClientPreference);
        });
    }

    private OrderedMap<String, String> createClientFirstInput(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.appends(new String[]{"Numero Unico de Identificacion Personal", "Nombre del cliente"},
                   new Component[]{NUMERIC_FIELD.get(), TEXT_FIELD.get()})
          .append("Tipo de cliente?", new JComboBox<>(new String[]{"Estudiante",
                  "Estudiante Graduado",
                  "Empleado",
                  "Profesor",
                  "Familiar de estudiante",
                  "Profesor invitado",
                  "Invitado a evento"}));
        int first = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (first == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput();
    }

    private OrderedMap<String, String> createClientSecondInput(String methodName) {
        InputBuilder ib = new InputBuilder();
        ib.appends(new String[]{"Amueblado?", "Compartido?", "Que tenga wifi?", "Que tenga cocineta?"},
                   new Component[]{YES_NO.get(), YES_NO.get(), YES_NO.get(), YES_NO.get()});
        int second = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
        if (second == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        return ib.getInput();
    }

    void createBooking() {
        String methodName = "createBooking";
        catcher(() -> {
            InputBuilder ib = new InputBuilder();
            ib.appends(new String[]{"NUIP del Cliente", "Nombre del Operador"},
                       new Component[]{NUMERIC_FIELD.get(), TEXT_FIELD.get()})
              .append("Costo", NUMERIC_FIELD.get())
              .appends(new String[]{"Inicio", "Fin"}, new Component[]{DATE.get(), DATE.get()});
            int selected = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
            if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
            return ib.getInput().getValues();
        }, business::createHostOffer);
    }

    void createOffer() {
        String methodName = "createOffer";
        catcher(() -> pkBooking(methodName), business::createReservation);
    }

    void createGroupReservation() {
        String methodName = "createGroupReservation";
        catcher(() -> {
            InputBuilder ib = new InputBuilder();
            ib.append("De cuantas personas es el grupo?", NUMERIC_FIELD.get());
            int selected = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
            if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
            InputBuilder ib2 = new InputBuilder();
            IntStream.range(1, Integer.parseInt(ib.getInput().get(0)) + 1)
                     .mapToObj(i->String.format("ID de la reserva para el cliente %02d", i))
                     .forEach(label -> ib2.append(label, NUMERIC_FIELD.get()));
            int selected2 = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
            if (selected2 == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
            return ib2.getInput().getValues();
        }, business::createGroupReservation);
    }

    void createHotel() {
        String methodName = "createHotel";
        catcher(() -> {
            InputBuilder ib = new InputBuilder();
            ib.append("NIT", NUMERIC_FIELD.get())
              .appends(
                      new String[]{"Tiene restaurante?", "Tiene piscina?", "Tiene parqueadero?", "Tiene wifi?", "Tiene television por cable?",},
                      new Component[]{YES_NO.get(), YES_NO.get(), YES_NO.get(), YES_NO.get(), YES_NO.get()});
            int selected = JOptionPane.showConfirmDialog(null, ib.build(), methodName, JOptionPane.OK_CANCEL_OPTION);
            if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
            return ib.getInput().getValues();
        }, business::createHotel);
    }

    Set<OrderedMap<String, String>> createServices()
            throws Exception {
        Set<OrderedMap<String, String>> serviceSet = new HashSet<>();
        Creator service = () -> {
            InputBuilder ib = new InputBuilder();
            ib.append("Nombre del servicio", TEXT_FIELD.get()).append("Costo del servicio", NUMERIC_FIELD.get());
            int dialog = JOptionPane.showConfirmDialog(null, ib.build(), "Servicio", JOptionPane.OK_CANCEL_OPTION);
            OrderedMap<String, String> input = ib.getInput();
            return dialog == JOptionPane.CANCEL_OPTION
                    ? null
                    : input;
        };

        InputBuilder builder = new InputBuilder().append("Cuantos servicios?", NUMERIC_FIELD.get());
        int selected = JOptionPane.showConfirmDialog(null, builder.build(), "Cuantos?", JOptionPane.OK_CANCEL_OPTION);
        if (selected == JOptionPane.CANCEL_OPTION) throw new UserException("CANCEL");
        int serviceQ = Integer.parseInt(builder.getInput().get(0));
        for (int i = 0; i < serviceQ; i++) {
            OrderedMap<String, String> map = service.get();
            if (map == null) {
                i--;
                continue;
            }
            serviceSet.add(map);
        }
        return serviceSet;
    }
    //</editor-fold>

    //<editor-fold desc="R.Two_Level">
    void retrieveOneApartmentSpec() {
        String methodName = "retrieveOneApartmentSpec";
        catcher(() -> pkApartment(methodName),
                input -> business.retrieveOne(Operator.class, input) + "\n"
                         + business.retrieveOne(OperatorSpec.class, input) + "\n"
                         + business.retrieveOne(OperatorService.class, input) + "\n"
                         + business.retrieveOne(ApartmentSpec.class, input));
    }

    void retrieveOneBooking() {
        String methodName = "retrieveOneBooking";
        catcher(() -> pkBooking(methodName),
                input -> business.retrieveOne(HostOffer.class, input) + "");
    }

    void retrieveOneClient() {
        String methodName = "retrieveOneClient";
        catcher(() -> pkClient(methodName),
                input -> business.retrieveOne(Client.class, input) + "\n"
                         + business.retrieveOne(ClientPreference.class, input));
    }

    void retrieveOneHostelSpec() {
        String methodName = "retrieveOneHostelSpec";
        catcher(() -> pkHostel(methodName),
                input -> business.retrieveOne(Operator.class, input) + "\n"
                         + business.retrieveOne(OperatorSpec.class, input) + "\n"
                         + business.retrieveOne(OperatorService.class, input) + "\n"
                         + business.retrieveOne(HostelSpec.class, input));
    }

    void retrieveOneHotel() {
        String methodName = "retrieveOneHotel";
        catcher(() -> pkHotel(methodName),
                input -> business.retrieveOne(Hotel.class, input) + "");
    }

    void retrieveOneHotelRoomSpec() {
        String methodName = "retrieveOneHotelRoomSpec";
        catcher(() -> pkHotelRoom(methodName),
                input -> business.retrieveOne(Operator.class, input) + "\n"
                         + business.retrieveOne(OperatorSpec.class, input) + "\n"
                         + business.retrieveOne(OperatorService.class, input) + "\n"
                         + business.retrieveOne(HotelRoomSpec.class, input));
    }

    void retrieveOneHouseRoomSpec() {
        String methodName = "retrieveOneHouseRoomSpec";
        catcher(() -> pkHouseRoom(methodName),
                input -> business.retrieveOne(Operator.class, input) + "\n"
                         + business.retrieveOne(OperatorSpec.class, input) + "\n"
                         + business.retrieveOne(OperatorService.class, input) + "\n"
                         + business.retrieveOne(HouseRoomSpec.class, input));
    }

    void retrieveOneOffer() {
        String methodName = "retrieveOneOffer";
        catcher(() -> pkOffer(methodName),
                input -> business.retrieveOne(GroupReservation.class, input) + "\n"
                         + business.retrieveOne(HostOffer.class, input));
    }

    void retrieveOneResidenceSpec() {
        String methodName = "retrieveOneResidenceSpec";
        catcher(() -> pkResidence(methodName),
                input -> business.retrieveOne(Operator.class, input) + "\n"
                         + business.retrieveOne(OperatorSpec.class, input) + "\n"
                         + business.retrieveOne(OperatorService.class, input) + "\n"
                         + business.retrieveOne(ResidenceSpec.class, input));
    }

    void retrieveOneRoomsHotel() {
        String methodName = "retrieveOneRoomsHotel";
        catcher(() -> pkRoomsHotel(methodName),
                input -> business.retrieveOne(Operator.class, input) + "\n"
                         + business.retrieveOne(HotelRoomSpec.class, input) + "\n"
                         + business.retrieveOne(RoomsHotel.class));
    }

    void retrieveOneStudentResSpec() {
        String methodName = "retrieveOneStudentResSpec";
        catcher(() -> pkStudentRes(methodName), input -> business.retrieveOne(Operator.class, input) + "\n"
                                                         + business.retrieveOne(HotelRoomSpec.class, input) + "\n"
                                                         + business.retrieveOne(StudentResSpec.class, input));
    }

    void retrieveOneGroupReservation() {
        String methodName = "retrieveOneGroupReservation";
        catcher(() -> pkGroupReservation(methodName), input -> business.retrieveGroupReservation(input) + "");
    }
    //</editor-fold>

    //<editor-fold desc="R.Two_Level">
    void retrieveAllApartmentSpec() {
        simpleCatcher(() -> business.retrieveAll(ApartmentSpec.class));
    }

    void retrieveAllBooking() {
        simpleCatcher(() -> business.retrieveAll(HostOffer.class));
    }

    void retrieveAllClient() {
        simpleCatcher(() -> business.retrieveAll(Client.class));
    }

    void retrieveAllClientPreference() {
        simpleCatcher(() -> business.retrieveAll(ClientPreference.class));
    }

    void retrieveAllHostelSpec() {
        simpleCatcher(() -> business.retrieveAll(HostelSpec.class));
    }

    void retrieveAllHotel() {
        simpleCatcher(() -> business.retrieveAll(Hotel.class));
    }

    void retrieveAllHotelRoomSpec() {
        simpleCatcher(() -> business.retrieveAll(HotelRoomSpec.class));
    }

    void retrieveAllHouseRoomSpec() {
        simpleCatcher(() -> business.retrieveAll(HouseRoomSpec.class));
    }

    void retrieveAllOffer() {
        simpleCatcher(() -> business.retrieveAll(GroupReservation.class));
    }

    void retrieveAllOperator() {
        simpleCatcher(() -> business.retrieveAll(Operator.class));
    }

    void retrieveAllOperatorService() {
        simpleCatcher(() -> business.retrieveAll(OperatorService.class));
    }

    void retrieveAllOperatorSpec() {
        simpleCatcher(() -> business.retrieveAll(OperatorSpec.class));
    }

    void retrieveAllResidenceSpec() {
        simpleCatcher(() -> business.retrieveAll(ResidenceSpec.class));
    }

    void retrieveAllRoomsHotel() {
        simpleCatcher(() -> business.retrieveAll(RoomsHotel.class));
    }

    void retrieveAllServiceScheme() {
        simpleCatcher(() -> business.retrieveAll(ServiceScheme.class));
    }

    void retrieveAllStudentResSpec() {
        simpleCatcher(() -> business.retrieveAll(StudentResSpec.class));
    }
    //</editor-fold>

    //<editor-fold desc="D.Two_Level">
    void deleteApartmentSpec() {
        String methodName = "deleteApartmentSpec";
        catcher(() -> pkApartment(methodName),
                input -> business.delete(Operator.class, input) + "\n"
                         + business.delete(OperatorSpec.class, input) + "\n"
                         + business.delete(OperatorService.class, input) + "\n"
                         + business.delete(ApartmentSpec.class, input));
    }

    void deleteBooking() {
        String methodName = "deleteBooking";
        catcher(() -> pkBooking(methodName),
                input -> business.delete(HostOffer.class, input));
    }

    void deleteClient() {
        String methodName = "deleteClient";
        catcher(() -> pkClient(methodName),
                input -> business.delete(Client.class, input) + "\n"
                         + business.delete(ClientPreference.class, input));
    }

    void deleteHostelSpec() {
        String methodName = "deleteHostelSpec";
        catcher(() -> pkHostel(methodName),
                input -> business.delete(Operator.class, input) + "\n"
                         + business.delete(OperatorSpec.class, input) + "\n"
                         + business.delete(OperatorService.class, input) + "\n"
                         + business.delete(HostelSpec.class, input));
    }

    void deleteHotel() {
        String methodName = "deleteHotel";
        catcher(() -> pkHotel(methodName),
                input -> business.delete(Hotel.class, input));
    }

    void deleteHotelRoomSpec() {
        String methodName = "deleteHotelRoomSpec";
        catcher(() -> pkHotelRoom(methodName),
                input -> business.delete(Operator.class, input) + "\n"
                         + business.delete(OperatorSpec.class, input) + "\n"
                         + business.delete(OperatorService.class, input) + "\n"
                         + business.delete(HotelRoomSpec.class, input));
    }

    void deleteHouseRoomSpec() {
        String methodName = "deleteHouseRoomSpec";
        catcher(() -> pkHouseRoom(methodName),
                input -> business.delete(Operator.class, input) + "\n"
                         + business.delete(OperatorSpec.class, input) + "\n"
                         + business.delete(OperatorService.class, input) + "\n"
                         + business.delete(HouseRoomSpec.class, input));
    }

    void deleteOffer() {
        String methodName = "deleteOffer";
        catcher(() -> pkBooking(methodName),
                input -> business.changeStatusBooking(false, input));
    }

    void deleteGroupReservation() {
        String methodName = "deleteGroupReservation";
        catcher(() -> new LinkedList<>() {{
            add(false);
            addAll(Arrays.asList(pkGroupReservation(methodName)));
        }}.toArray(), business::changeStatusGroupBooking);
    }

    void deleteResidenceSpec() {
        String methodName = "deleteResidenceSpec";
        catcher(() -> pkResidence(methodName),
                input -> business.delete(Operator.class, input) + "\n"
                         + business.delete(OperatorSpec.class, input) + "\n"
                         + business.delete(OperatorService.class, input) + "\n"
                         + business.delete(ResidenceSpec.class, input));
    }

    void deleteRoomsHotel() {
        String methodName = "deleteRoomsHotel";
        catcher(() -> pkRoomsHotel(methodName),
                input -> business.delete(Operator.class, input) + "\n"
                         + business.delete(HotelRoomSpec.class, input) + "\n"
                         + business.delete(RoomsHotel.class, input));
    }

    void deleteStudentResSpec() {
        String methodName = "deleteStudentResSpec";
        catcher(() -> pkStudentRes(methodName),
                input -> business.delete(StudentResSpec.class, input)
        );
    }
    //</editor-fold>

    //<editor-fold desc="Extras">
    void cleanAlohandesLog() {
        try (Writer w = new BufferedWriter(new FileWriter("alohandes.log"))) {
            w.write("");
            console.print("AlohandesB.log limpiado exitosamente", null);
        } catch (IOException e) {
            console.print(e.getMessage(), false);
        }
    }

    void cleanDatanucleusLog() {
        try (Writer w = new BufferedWriter(new FileWriter("datanucleus.log"))) {
            w.write("");
            console.print("Datanucleus.log limpiado exitosamente", null);
        } catch (IOException e) {
            console.print(e.getMessage(), false);
        }
    }

    void about() {
        console.print("Equipo B-04::\n"
                      + TextTable.builder().append(new Object[]{"Pedro Lobato"}),
                      null);
    }

    void clear() {
        console.print("Limpiando consola...", null);
        console.clear();
    }

    void exit() {
        console.print("Saliendo...", null);
        System.exit(0);
    }

    public void dark() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            UIManager.put("edu.uniandes.console.status.null", UIConfig.DARK_STATUS[0]);
            UIManager.put("edu.uniandes.console.status.true", UIConfig.DARK_STATUS[1]);
            UIManager.put("edu.uniandes.console.status.false", UIConfig.DARK_STATUS[2]);
            console.reset();
            DARK = true;
            SwingUtilities.updateComponentTreeUI(this);
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
    }

    public void light() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("edu.uniandes.console.status.null", UIConfig.LIGHT_STATUS[0]);
            UIManager.put("edu.uniandes.console.status.true", UIConfig.LIGHT_STATUS[1]);
            UIManager.put("edu.uniandes.console.status.false", UIConfig.LIGHT_STATUS[2]);
            console.reset();
            DARK = false;
            SwingUtilities.updateComponentTreeUI(this);
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
    }
    //</editor-fold>

    @FunctionalInterface interface Executable {
        Object[] call()
                throws Exception;
    }

    @FunctionalInterface interface Monad {
        String apply(Object[] strings)
                throws Exception;
    }

    @FunctionalInterface interface Creator {
        OrderedMap<String, String> get()
                throws Exception;
    }

    @Deprecated private void catcher(Executable caller,
                         Monad lambda) {
        String result;
        boolean b;
        try {
            result = lambda.apply(caller.call());
            b = true;
        } catch (Exception | AssertionError e) {
            LOG.error(e.getMessage());
            result = generateErrorMessage(e);
            b = false;
        }
        console.print(result, b);
    }

    private void simpleCatcher(Callable<String> lambda) {
        String result;
        boolean b;
        try {
            result = lambda.call();
            b = true;
        } catch (Exception | AssertionError e) {
            LOG.error(e.getMessage());
            result = generateErrorMessage(e);
            b = false;
        }
        console.print(result, b);
    }

    private void terminal(java.util.function.Function<? super String, Object[]> input, Monad business, String methodName) {
        catcher(() -> input.apply(methodName), business);
    }
}
