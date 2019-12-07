/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.item;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public final class Book extends ExtendedItemBuilder<BookMeta> {

    /**
     * creates new Book with predefined variables
     * @param amount item amount in ItemStack
     * @param title title of the book
     * @param author author of the book
     * @param pages pages as Stings, may not be longer than 256 chars
     */
    public Book(int amount, String title, String author, String... pages) {
        this(amount);
        setTitle(title);
        setAuthor(author);
        setPages(pages);
    }

    /**
     * creates new Book instance with item amount 1
     */
    public Book() {
        this(1);
    }

    /**
     * creates new Book instance
     * @param amount amount of items in ItemStack
     */
    public Book(int amount) {
        super(new ItemStack(Material.WRITTEN_BOOK, amount));
    }

    private Book(ItemStack book) {
        super(book);
    }

    /**
     * wraps an existing ItemStack which must be of Material.WRITTEN_BOOK or Material.BOOK_AND_QUILL in an Book object
     * @param book ItemStack
     * @return new Book instance
     * @throws ClassCastException if ItemStack has a conflicting Material
     */
    public static Book wrap(ItemStack book) throws ClassCastException {
        BookMeta meta = (BookMeta) book.getItemMeta();
        return new Book(book);
    }

    /**
     * Sets the title of the book.
     * Limited to 16 characters. Removes title when given null.
     * @param title the title to set
     * @throws UnsupportedOperationException if title can not be set
     * @return this
     */
    public Book setTitle(String title) {
        if (!meta.setTitle(title)) {
            throw new UnsupportedOperationException("Title could not be set");
        }
        return this;
    }

    /**
     * Sets the author of the book. Removes author when given null.
     * @param author the author of the book
     * @return this
     */
    public Book setAuthor(String author) {
        meta.setAuthor(author);
        return this;
    }

    /**
     * Sets the specified page in the book. Pages of the book must be contiguous.
     * The packets can be up to 256 characters in length, additional characters are truncated.
     * @param number the page number to set
     * @param text the packets to set for that page
     * @return this
     */
    public Book setPage(int number, String text) {
        meta.setPage(number, text);
        return this;
    }

    /**
     * Clears the existing book pages, and sets the book to use the provided pages. Maximum 50 pages with 256 characters per page.
     * @param pages a list of pages to set the the book to use
     * @return this
     */
    public Book setPages(List<String> pages) {
        meta.setPages(pages);
        return this;
    }

    /**
     * Clears the existing book pages, and sets the book to use the provided pages. Maximum 50 pages with 256 characters per page.
     * @param pages A list of strings, each being a page
     * @return this
     */
    public Book setPages(String... pages) {
        meta.setPages(pages);
        return this;
    }

    /**
     * Adds new pages to the end of the book. Up to a maximum of 50 pages with 256 characters per page.
     * @param pages A list of strings, each being a page
     * @return this
     */
    public Book addPage(String... pages) {
        meta.addPage(pages);
        return this;
    }

    /**
     * Checks for the existence of a title in the book
     * @return true if book has title
     */
    public boolean hasTitle() {
        return meta.hasTitle();
    }

    /**
     * Gets the title of the book.
     * Plugins should check that hasTitle() returns true before calling this method.
     * @return the title of the book
     */
    public String getTitle() {
        return meta.getTitle();
    }

    /**
     * Checks for the existence of an author in the book.
     * @return true if book has an author
     */
    public boolean hasAuthor() {
        return meta.hasAuthor();
    }

    /**
     * Gets the author of the book.
     * Plugins should check that hasAuthor() returns true before calling this method.
     * @return the author of the book
     */
    public String getAuthor() {
        return meta.getAuthor();
    }

    /**
     * Checks for the existence of pages in the book.
     * @return true if book has pages
     */
    public boolean hasPages() {
        return meta.hasPages();
    }

    /**
     * Gets the specified page in the book. The given page must exist.
     * @param i the page number to get
     * @return the page from the book
     */
    public String getPage(int i) {
        return meta.getPage(i);
    }

    /**
     * Gets all the pages in the book.
     * @return list of all pages in the book
     */
    public List<String> getPages() {
        return meta.getPages();
    }

    /**
     * Gets the number of pages in the book.
     * @return the number of pages in the book
     */
    public int getPageCount() {
        return meta.getPageCount();
    }

    /**
     * Opens the book for a specific player
     * @param player player
     * @return this
     */
    public Book open(Player player) {
        CoreSystem.getInstance().openBook(player, getItemStack());
        return this;
    }

}
