package ch.epfl.cs107.play;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.net.HttpURLConnection.*;

public final class Submit {
    // CONFIGURATION
    // -------------
    // Jeton du premier membre du groupe
    private static final String TOKEN_1 = "eeg0Eela";
    // Jeton du second membre (identique au premier pour les personnes travaillant seules)
    private static final String TOKEN_2 = "Ei0aeda5";
    // Les fichiers se trouvant dans l'un de ces répertoires sont ignorés.
    private static final Set<Path> DIRECTORIES_TO_IGNORE =
            Set.of(Path.of("out"), Path.of(".idea"), Path.of("src", ".idea"));
    // Les fichiers dont le nom commence par l'un de ces préfixes sont inclus dans le rendu.
    private static final Set<String> PREFIXES_TO_SUBMIT =
            Set.of("readme", "conception");
    // Les fichiers dont le nom se termine par l'un de ces suffixes sont inclus dans le rendu.
    private static final Set<String> SUFFIXES_TO_SUBMIT =
            Set.of(".java", ".png", ".ttf", ".wav", ".xml", ".txt", ".md", ".pdf");
    // Les fichiers dont la somme SHA-1 est l'une de celles ci-dessous ne sont *pas* inclus dans le rendu.
    //<editor-fold desc="SHA-1 sums">
    private static final Set<String> GIVEN_RESOURCES_SHA1 = Set.of(
            "cb481ac41ed7748891342c5a1dca383dfe893242", // records/zelda/Chateau1.xml
            "29741a1135c141dd889efa3db4c124ab2eaf632d", // records/zelda/Ferme1.xml
            "7be2aa5d8a9a8ac61efb302b5b785b6417c6647d", // records/zelda/Ferme2.xml
            "dc58cf4f1c98ec0b77374ee14e1aa56b76122f33", // records/zelda/Route1.xml
            "0b5c5fe44cd96768d74bf58040e29dc15dcbe363", // records/zelda/Route2.xml
            "9c03349be70e752084909e12fcec3ce5289dfc33", // records/zelda/RouteChateau1.xml
            "d6501e13fab035ba5950946af36a2679b629b9e4", // records/zelda/RouteTemple1.xml
            "93c560a300b5c77b9337cb8b2808e983feedbb21", // records/zelda/RouteTemple2.xml
            "62e213deebf80c7815e1e4412584185205a41511", // records/zelda/Temple1.xml
            "98571dcf77146f5cc63a80e0fa9dddb1ef7074e1", // records/zelda/Village1.xml
            "40e93e31c416f53ead25437cf38b22adb760e2d7", // records/zelda/Village2.xml
            "f8540df7288fb0ab60f70fa21edb31b924cf2bf4", // res/fonts/Dragonfly.ttf
            "e6d110fc65132416d6ebf2a3b39283ec5a722235", // res/fonts/Kenney Pixel.ttf
            "c1691e8168b2596af8a00162bac60dbe605e9e36", // res/fonts/OpenSans-Bold.ttf
            "cea7b25e625f8f42d09c3034aad8afd826e9941a", // res/fonts/OpenSans-BoldItalic.ttf
            "97d0bd44f1731a9d97fc29e2b2208835575b60b9", // res/fonts/OpenSans-ExtraBold.ttf
            "1d865d0a3e05f13ce91e3727e0cb97fdc1c4ca42", // res/fonts/OpenSans-ExtraBoldItalic.ttf
            "f1692eac564e95023e4da341a1b89baae7a65155", // res/fonts/OpenSans-Italic.ttf
            "40442c189568184b6e6c27a25d69f14d91b65039", // res/fonts/OpenSans-Light.ttf
            "73c4730894c67900e0b7bd7a491c717d4a236b30", // res/fonts/OpenSans-LightItalic.ttf
            "3564ed0b5363df5cf277c16e0c6bedc5a682217f", // res/fonts/OpenSans-Regular.ttf
            "f1ee7a9c6d13ee2d642a806c09e737275e613792", // res/fonts/OpenSans-Semibold.ttf
            "f577ce4322c761a10d093f11f077a48602d2c076", // res/fonts/OpenSans-SemiboldItalic.ttf
            "31b38d10142e3c5c07d3401fcb0569e0d2869978", // res/images/backgrounds/icrogue/Level0Room.png
            "29f73920dd4a3943335874e30e876b33ab5b67a8", // res/images/backgrounds/zelda/Ferme.png
            "5f763b10b71c9ed770808ff522dae8867358cadb", // res/images/backgrounds/zelda/Village.png
            "31f98cdc129264593fd4ee9b4670db77bf2ea6ff", // res/images/behaviors/icrogue/Level0Room.png
            "b7315a8a2808edc16c2e540cbfe5bb135b9eb8a1", // res/images/behaviors/zelda/Ferme.png
            "f60448a194ed1031c9b63163839dab46653f16c8", // res/images/behaviors/zelda/Village.png
            "787cb0b28c1ae71e8d2c69f6ed767985c1be30e3", // res/images/foregrounds/PetalburgArena1.png
            "793bf40f7ba4c6b7c9ed54bb4543c1c22edc8beb", // res/images/foregrounds/PetalburgCenter.png
            "06f6a0827b7658ba5242ba99d9b0e6f3ff0fbdf7", // res/images/foregrounds/PetalburgCity.png
            "30a4da17af33d57264fd66e05c3c30cc66e5cb23", // res/images/foregrounds/PetalburgShop.png
            "07aef7a0eaaa20a9cfe7d484e5eb4f9b11cfb946", // res/images/foregrounds/PetalburgTimmy.png
            "5b9092c3a1ee8552e5ccf9b69e64b964f67942b7", // res/images/foregrounds/bag.png
            "2d6a3d906df8a778caf3b4f6d5620ecf84b1bf3e", // res/images/foregrounds/icrogue/Level0Room.png
            "d91479e0505fb112376742362c975f465de75c1d", // res/images/foregrounds/icrogue/level0.png
            "ebe494c52c8301ee6ddfecf662d3c94f9cb6f613", // res/images/foregrounds/lightHalo.png
            "f3511ccac671ec3a6c773aab725bad0a1421c504", // res/images/foregrounds/zelda/Ferme.png
            "03120ed4c73b875656d863e5241df677eb7aa8db", // res/images/foregrounds/zelda/Village.png
            "5e8c8451424914abbc94f96d61dbb7cddecff9b8", // res/images/sprites/001.png
            "9bfaef662a1700537246251d551e16f79e324cb5", // res/images/sprites/031.png
            "4a6b857cc9656619087ac7bdbcc4f4c76a5e440a", // res/images/sprites/408.png
            "865e0c36f3e2b0d18be1b08f73ac3da7c85e939f", // res/images/sprites/Bachelorball.png
            "8960173eba5ba2da0765727a6bdaeaf4eddb74e3", // res/images/sprites/Bonus.png
            "3435a7b8e0dd881025d79d54f4d61573bdbf33a1", // res/images/sprites/Cherry.png
            "1881b51c1881f78f5012b9b9dd449f1cf8801a12", // res/images/sprites/Diamond.png
            "b0c8f293a2aac6c1175f518ecc7a7fcb867710a6", // res/images/sprites/GroundLightOff.png
            "89aa419331971cd7f0298584f4bd63a62cc574d9", // res/images/sprites/GroundLightOn.png
            "7fda0388b0ef4b9f2ae51f0b06582de49fd7ab8c", // res/images/sprites/GroundPlateOff.png
            "38e70f696e139b1c6b0cb6d3607da7fe39a19bd4", // res/images/sprites/HPbar.png
            "b9f708512b2f7b849cb29f6d7eb355a566b196fc", // res/images/sprites/Icball.png
            "06d2c173c82f182b9b7a4b3c590b4068ceda41dd", // res/images/sprites/Inball.2.png
            "6653867b448fa1db68115ba9e6642ef4ed1fe364", // res/images/sprites/Inball.png
            "04d83e54cef6050f20e0e8509fcccfe1264567e2", // res/images/sprites/Letterball.png
            "37779a2eb4a1a7b17a54177ed5aadea388fd9cbf", // res/images/sprites/LeverDown.png
            "2ebeaab4b82c43349b997500acadb6e79e91e32e", // res/images/sprites/LeverUp.png
            "b4e56a7f39271069f9a39649324ed3cecf1ad06c", // res/images/sprites/Manball.png
            "f447393cee19e3bbae71b373535f4ce31e0265d6", // res/images/sprites/Masterball.png
            "902c8d8123b5db1b81c561e2dbf336b8d6030dfc", // res/images/sprites/Mecaball.png
            "89139334bd1ceacc38866184055a0a9615714e8e", // res/images/sprites/Satball.png
            "61369e9b59bbee0a75df12774d6f653c5900ace3", // res/images/sprites/Syscomball.png
            "1e56cbd26eb3b055cbcad71ef0201f43e3846bd9", // res/images/sprites/XPbar.png
            "80f56cb092ca5c80e9691d2da65e2717d69784ff", // res/images/sprites/assistant.fixed.png
            "4a5c5de55c5777825203eb750e7fe5907303f1b3", // res/images/sprites/bag.png
            "c05ca2188c971558115f5eb52687502727ef8b2e", // res/images/sprites/boy.1.png
            "f8c998b52c2c946391ee578113209e3d51bee3c1", // res/images/sprites/cellOver.png
            "693f8b2d02802562d9f955a09eba6c54feec5cdf", // res/images/sprites/dialog.png
            "38b2b49dd998bfca1adc86f0bed3a3e1d2d3534a", // res/images/sprites/flora.png
            "08d53895411b42372d02913ec7d19f13e479d02f", // res/images/sprites/ghost.1.png
            "0cc377c19607d54d81400ce32512d95ada8ef8df", // res/images/sprites/ghost.2.png
            "469809113ce701975f8e1cedb2e28070402ea29e", // res/images/sprites/girl.1.png
            "023604f559123335cd06df5b14ec7f78b67b2df3", // res/images/sprites/icrogue/cherry.png
            "d060f4111a7378205db25df8ef2b3038042260a6", // res/images/sprites/icrogue/door_0.png
            "786222bd9fe134ae71b3a1b410edc579499b6d14", // res/images/sprites/icrogue/door_1.png
            "f1bb40d5849e8ba1f854aadb64a1d1eeed49cd58", // res/images/sprites/icrogue/door_2.png
            "c5a67b40557539a7326ad88e4f447ede2165dc98", // res/images/sprites/icrogue/door_3.png
            "f08da1f398322a5493298d9de5bbc80bd935a61b", // res/images/sprites/icrogue/invisibleDoor_0.png
            "cb66094b0e4624c70a9af066e2806e2dede320f8", // res/images/sprites/icrogue/invisibleDoor_1.png
            "8be640940add70315bcd63190f60523585c3c917", // res/images/sprites/icrogue/invisibleDoor_2.png
            "569b1fa2241838d7eeebbc1b9e8a8a8c4eb2597a", // res/images/sprites/icrogue/invisibleDoor_3.png
            "74f936c598994d4df062bbbf3ca13d12cc1d9895", // res/images/sprites/icrogue/key.png
            "49aa687b8dc1c364b082b2f7cdfe732d227c0310", // res/images/sprites/icrogue/lockedDoor_0.png
            "61d21d502dee95b09e114aab4f24e21dee43f85b", // res/images/sprites/icrogue/lockedDoor_1.png
            "46198199a2b015ce3f1b4aaed96290933d4d2bf6", // res/images/sprites/icrogue/lockedDoor_2.png
            "4adb29f4ea78e519796d00f53e43143a02aeffe3", // res/images/sprites/icrogue/lockedDoor_3.png
            "610a3dc624dba245551d27d987d61a665c1a2200", // res/images/sprites/icrogue/static_npc.png
            "6755e0c7b2e93e58007ad979005d9e75e5ac3aef", // res/images/sprites/joel.fixed.png
            "229a1b8f3f50305dbb7998a1d8430b396321a13b", // res/images/sprites/max.old.bike.png
            "e6c84cfb203b4283417fa028b13eef4d09859e97", // res/images/sprites/max.old.png
            "52fab503fa938f2938039d62533ba946e5164150", // res/images/sprites/max.old.surf.png
            "580501ca13ac1659d6caf612e49896dc0025a5d2", // res/images/sprites/max.png
            "793a440cef45f3fecf1373c5223b3a91430e32d7", // res/images/sprites/mew.fixed.png
            "cde7d30a09c98a1bacb1b4b09587037753295eda", // res/images/sprites/pacman.png
            "5345285a94f5ec91dc9f4324eb12f7ba552fe2fc", // res/images/sprites/player.png
            "960ac12ae464abe44cf572113fc2725c7e9cc751", // res/images/sprites/player_bicycle.png
            "cc51c353845ff57e874751706c3d5ee8ecffad19", // res/images/sprites/player_surf.png
            "2d8666f21bc1a6c36e71dcc7cd94b57abfc2b7cc", // res/images/sprites/policeman.png
            "75b84cab78d48ddc47a65df65833884e5654a591", // res/images/sprites/rock.1.png
            "4edbd8f0e25dfd91db7d4e815304cc3f092e5f72", // res/images/sprites/rock.2.png
            "6bfb2976e4cfda50d649cb19c51a5349a3a1e68e", // res/images/sprites/rock.3.png
            "a130139a8e49c6f2f21a622b5adb7ee8e3611b39", // res/images/sprites/scoreboard.png
            "84b8f3bd43615b37eaa64849ce5272f2a498d3b2", // res/images/sprites/shadow.png
            "f583dcd6240335c3ce0028ebedb24109c6b03537", // res/images/sprites/shopAssistant.png
            "bb2cb7a7ed0c339f62d8a792ee2e5085a1d8772b", // res/images/sprites/zelda/arrow.icon.png
            "ca0e9f869a86b994385c705f0f85d4b0faba7fa7", // res/images/sprites/zelda/arrow.png
            "2f8319c59e7bc128c91bb333e8d15733c3ab843d", // res/images/sprites/zelda/bomb.png
            "a360d5b025feab1165501e987ada668f24c6197b", // res/images/sprites/zelda/bow.icon.png
            "d1f1bdb3e2ce8c7a2d9bcf578876388518ba23e8", // res/images/sprites/zelda/bridge.png
            "4f5e06852a8a50544e77f439a56921a1fba9e94d", // res/images/sprites/zelda/castleDoor.close.png
            "7b94d455503726a24ff5efd962c6c21901ea688c", // res/images/sprites/zelda/castleDoor.open.png
            "0ea50b26d04de939de1a8cf57a5d3c92d3790100", // res/images/sprites/zelda/cave.close.png
            "8866e93ecb7c4479e0043c037dece6b80e904188", // res/images/sprites/zelda/cave.open.png
            "44d57efcb03ebd582be3af08989c21a29681b991", // res/images/sprites/zelda/character.png
            "ddd6abfe819c9e49636c6d49a9947f3304431f12", // res/images/sprites/zelda/coin.png
            "d8515e06d350cd1bf7c6dcadda34a3c221dbbb91", // res/images/sprites/zelda/coinsDisplay.png
            "2b07145fdc84b589009c037240ea84e5f7fd66a6", // res/images/sprites/zelda/darkLord.png
            "431f37e76f748912314fa22eb60d3c9275b66f18", // res/images/sprites/zelda/darkLord.spell.png
            "924b597443490856cc9e38f43f09b01be424f95c", // res/images/sprites/zelda/dialog.png
            "27023371622de887d3cba447ea08b923f2e99dce", // res/images/sprites/zelda/digits.png
            "4f5ca04bb390e8afc62865218dde02d3d6768de4", // res/images/sprites/zelda/explosion.png
            "4d1efc5af986e60595b8e83e22d46d03f47028c6", // res/images/sprites/zelda/fire.png
            "3e1bfd62bf84f43718cd07954f0526a10f0308cb", // res/images/sprites/zelda/flameskull.png
            "b729695e6f4e1d50f325160ce3f90ea1ca7b3d3b", // res/images/sprites/zelda/gearDisplay.png
            "a832591d13d3a401b2d6d5ca35d113744c951c60", // res/images/sprites/zelda/grass.png
            "190c2df57d76796da79f9ba81b23cf95c976bee9", // res/images/sprites/zelda/grass.sliced.png
            "b0edf470eda8d903d4689378703ba0aa2d1ddedf", // res/images/sprites/zelda/heart.png
            "d283e780e482831d7f8e77957dd85611d3e54196", // res/images/sprites/zelda/heartDisplay.png
            "b6901d5d0beb9615198db547da790356f4dfaeca", // res/images/sprites/zelda/inventory.background.png
            "bddbf50d905f31a10f4a9ec56de46f416ff3809b", // res/images/sprites/zelda/inventory.selector.png
            "04ac2ff8dc9d8b804421b755d588c85da1fa46bb", // res/images/sprites/zelda/inventory.slot.png
            "1c5035c894c1894041d1f19f3842711a5bbd3255", // res/images/sprites/zelda/key.png
            "02edc140ac48361bb1f72256422127f4b36d37c4", // res/images/sprites/zelda/king.png
            "b13ad79475f5407bb1e199759316edcdf9d4854f", // res/images/sprites/zelda/logMonster.png
            "8eb2123013bf7b5ee5f4828a737394a61761dcf7", // res/images/sprites/zelda/logMonster.sleeping.png
            "1dfd362ab75f8c26164f30f4aaf141afe1b455cb", // res/images/sprites/zelda/logMonster.wakingUp.png
            "7e0421cb4230d44a7b79bc29afa77b6ee80bdbdb", // res/images/sprites/zelda/magicWaterProjectile.png
            "cf68b2a2162e315d57974b5dbba9e4c2b7204506", // res/images/sprites/zelda/orb.png
            "fd73b6b001313c0072512f70a6389cb87c0a1b67", // res/images/sprites/zelda/player.bow.png
            "74e3421be0105852fdfe3523eebd15fef62b7a93", // res/images/sprites/zelda/player.png
            "0698b94a53749fbbbc6869f3f03dc967d6af4c61", // res/images/sprites/zelda/player.staff_water.png
            "34cc359a1ff63ec58f4313c5a4fb01956a66a216", // res/images/sprites/zelda/player.sword.png
            "c4eb64380cdcecb2cd9ed917f9bef7dd99186df8", // res/images/sprites/zelda/selector.png
            "fc7a8e2d9ea9d568c7521ba8a83f8aae2e51b650", // res/images/sprites/zelda/staff.png
            "5e27479a500d0f0d7369a2ec432d237962c508db", // res/images/sprites/zelda/staff_water.icon.png
            "71ff5602dc0fc5bbf29d846b718779c43d1964a2", // res/images/sprites/zelda/sword.icon.png
            "a511a7193d01d588f862d3785097ae13fb055677", // res/images/sprites/zelda/vanish.png
            "2eabf3c2cfcada07c7f18f9e5faa982f680ae325", // res/images/sprites/zelda/waterfall.png
            "6cf51a3a236c3b5479dbc40f678a12b5849265cb", // res/sounds/dialogNext.wav
            "3d34c8f6ad5a253380b8e416fa844b5726bd77fe"  // res/strings/enigme_fr.xml
    );

    //</editor-fold>
    // -------------

    private static final String ZIP_ENTRY_NAME_PREFIX = "MP2/";
    private static final int TOKEN_LENGTH = 8;
    private static final int TIMEOUT_SECONDS = 5;

    private static final String BASE32_ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";
    private static final Pattern SUBMISSION_ID_RX =
            Pattern.compile(
                    Stream.generate(() -> "[%s]{4}".formatted(BASE32_ALPHABET))
                            .limit(4)
                            .collect(Collectors.joining("-")));

    public static void main(String[] args) {
        var token1 = args.length >= 1 ? args[0] : TOKEN_1;
        var token2 = args.length >= 2 ? args[1] : TOKEN_2;

        if (token1.length() != TOKEN_LENGTH) {
            System.err.println("Erreur: vous n'avez correctement défini TOKEN_1 dans Submit.java !");
            System.exit(1);
        }
        if (token2.length() != TOKEN_LENGTH) {
            System.err.println("Erreur: vous n'avez correctement défini TOKEN_2 dans Submit.java !");
            System.exit(1);
        }

        try {
            var projectRoot = Path.of(System.getProperty("user.dir"));
            var paths = filesToSubmit(
                    projectRoot,
                    path -> {
                        var fileName = path.getFileName().toString().toLowerCase();
                        return DIRECTORIES_TO_IGNORE.stream().noneMatch(path::startsWith)
                                && !fileName.equals("submit.java")
                                && !GIVEN_RESOURCES_SHA1.contains(sha1Hash(path))
                                && (PREFIXES_TO_SUBMIT.stream().anyMatch(fileName::startsWith)
                                || SUFFIXES_TO_SUBMIT.stream().anyMatch(fileName::endsWith));
                    }
            );

            var zipArchive = createZipArchive(paths);
            var response = submitZip(token1 + token2, zipArchive);
            switch (response.statusCode()) {
                case HTTP_CREATED -> {
                    var subIdMatcher = SUBMISSION_ID_RX.matcher(response.body());
                    var subId = subIdMatcher.find() ? subIdMatcher.group() : "  ERREUR";
                    System.out.printf("""
                        Votre rendu a bien été reçu par le serveur et stocké sous le nom :
                          %s
                        Il est composé des fichiers suivants :
                          %s
                        Votre rendu sera prochainement validé et le résultat de cette
                        validation vous sera communiqué par e-mail, à votre adresse de l'EPFL.""",
                            subId,
                            paths.stream().map(Object::toString).collect(Collectors.joining("\n  ")));
                }
                case HTTP_ENTITY_TOO_LARGE -> System.err.println("Erreur : l'archive est trop volumineuse !");
                case HTTP_UNAUTHORIZED -> System.err.println("Erreur : au moins un des jetons est invalide !");
                case HTTP_BAD_GATEWAY -> System.err.println("Erreur : le serveur de rendu n'est pas actif !");
                default -> System.err.printf("Erreur : réponse inattendue (%s)", response);
            }

            System.exit(response.statusCode() == HTTP_CREATED ? 0 : 1);
        } catch (IOException | InterruptedException e) {
            System.err.println("Erreur inattendue !");
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static String sha1Hash(Path filePath) {
        try (var s = new FileInputStream(filePath.toFile())) {
            var sha1 = MessageDigest.getInstance("SHA-1");
            return HexFormat.of().formatHex(sha1.digest(s.readAllBytes()));
        } catch (NoSuchAlgorithmException e) { // This cannot happen, SHA-1 being available everywhere
            throw new Error(e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static List<Path> filesToSubmit(Path projectRoot, Predicate<Path> keepFile) throws IOException {
        try (var paths = Files.walk(projectRoot)) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(projectRoot::relativize)
                    .filter(keepFile)
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();
        }
    }

    private static byte[] createZipArchive(List<Path> paths) throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        try (var zipStream = new ZipOutputStream(byteArrayOutputStream)) {
            for (var path : paths) {
                var entryPath = IntStream.range(0, path.getNameCount())
                        .mapToObj(path::getName)
                        .map(Path::toString)
                        .collect(Collectors.joining("/", ZIP_ENTRY_NAME_PREFIX, ""));
                zipStream.putNextEntry(new ZipEntry(entryPath));
                try (var fileStream = new FileInputStream(path.toFile())) {
                    fileStream.transferTo(zipStream);
                }
                zipStream.closeEntry();
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    private static HttpResponse<String> submitZip(String submissionToken, byte[] zipArchive)
            throws IOException, InterruptedException {
        var httpRequest = HttpRequest.newBuilder(URI.create("https://cs108.epfl.ch/api_cs107/submissions"))
                .POST(HttpRequest.BodyPublishers.ofByteArray(zipArchive))
                .header("Authorization", "token %s".formatted(submissionToken))
                .header("Content-Type", "application/zip")
                .header("Accept", "text/plain")
                .header("Accept-Language", "fr")
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();

        return HttpClient.newHttpClient()
                .send(httpRequest, HttpResponse.BodyHandlers.ofString());
    }

    // For debugging
    private static void writeZip(Path filePath, byte[] zipArchive) throws IOException {
        try (var c = new FileOutputStream(filePath.toFile())) {
            c.write(zipArchive);
        }
    }
}
