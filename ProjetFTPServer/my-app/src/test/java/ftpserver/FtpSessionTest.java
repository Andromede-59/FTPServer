package ftpserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FtpSessionTest {

    private FtpSession ftpSession;
    private File rootDirectory;

    @BeforeEach
    void setUp() {
        rootDirectory = new File(System.getProperty("java.io.tmpdir"), "ftp-root");
        rootDirectory.mkdirs(); // Crée le répertoire temporaire de test
        ftpSession = new FtpSession(rootDirectory);
    }

    @Test
    void testResolveFileWithinRoot() throws IOException {
        File expectedFile = new File(rootDirectory, "test.txt");
        File resolvedFile = ftpSession.resolveFile("test.txt");

        assertEquals(expectedFile.getCanonicalPath(), resolvedFile.getCanonicalPath(),
                "Le fichier résolu doit être dans le répertoire racine.");
    }

    @Test
    void testResolveFileOutsideRoot() {
        assertThrows(SecurityException.class, () -> ftpSession.resolveFile("../../outside.txt"),
                "L'accès en dehors du répertoire racine doit être refusé.");
    }

    @Test
    void testSetCurrentDirectory() {
        File newDir = new File(rootDirectory, "subdir");
        newDir.mkdirs();

        ftpSession.setCurrentDirectory(newDir);
        assertEquals(newDir, ftpSession.getCurrentDirectory(),
                "Le répertoire courant doit être mis à jour correctement.");
    }

    @Test
    void testSetCurrentDirectoryOutsideRoot() {
        File outsideDir = new File("/outside");

        assertThrows(SecurityException.class, () -> ftpSession.setCurrentDirectory(outsideDir),
                "Le changement de répertoire en dehors du répertoire racine doit échouer.");
    }

    @Test
    void testSetAndGetTransferType() {
        ftpSession.setTransferType(TransferType.BINARY);
        assertEquals(TransferType.BINARY, ftpSession.getTransferType(),
                "Le type de transfert doit être correctement mis à jour.");

        ftpSession.setTransferType(TransferType.ASCII);
        assertEquals(TransferType.ASCII, ftpSession.getTransferType(),
                "Le type de transfert doit revenir à ASCII.");
    }

    @Test
    void testDataConnectionNotNull() {
        assertNotNull(ftpSession.getDataConnection(), "La connexion de données ne doit jamais être null.");
    }

    @Test
    void testSetAndGetRenameFile() {
        File fileToRename = new File(rootDirectory, "rename.txt");
        ftpSession.setRenameFile(fileToRename);

        assertEquals(fileToRename, ftpSession.getRenameFile(),
                "Le fichier de renommage doit être correctement défini et récupéré.");
    }
}
