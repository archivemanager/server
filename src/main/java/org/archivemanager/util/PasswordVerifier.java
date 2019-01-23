package org.archivemanager.util;
/**
 * Interface to verify passwords.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.00.00
 */
public interface PasswordVerifier {
    /**
     * Verify that this password is an OK password.  If a password
     * is not verified it is thrown out and a new password is tried.
     * Always returning false from this method will cause an infinite
     * loop.
     *
     * @param password an array of characters representing a password.
     * @return true iff this password is OK.
     *
     * @since ostermillerutils 1.00.00
     */
    public boolean verify(char[] password);
 
}