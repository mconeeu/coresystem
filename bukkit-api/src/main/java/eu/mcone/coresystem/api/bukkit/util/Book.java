/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.util;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class Book {

    private final ItemStack book;
    private final BookMeta meta;

    public Book(String title, String author, String... pages) {
        this();
        setTitle(title);
        setAuthor(author);
        setPages(pages);
    }

    public Book() {
        this.book = new ItemStack(Material.WRITTEN_BOOK, 1);
        this.meta = (BookMeta) book.getItemMeta();
    }

    private Book(ItemStack book) {
        this.book = book;
        this.meta = (BookMeta) book.getItemMeta();
    }

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
     * The data can be up to 256 characters in length, additional characters are truncated.
     * @param number the page number to set
     * @param text the data to set for that page
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
        CoreSystem.getInstance().openBook(player, toItemStack());
        return this;
    }

    /**
     * converts the Book to an ItemBuilder for further modifying
     * @return ItemBuilder
     */
    public ItemBuilder toItemBuilder() {
        return ItemBuilder.wrap(book);
    }

    /**
     * converts the Book to an ItemStack
     * @return ItemStack
     */
    public ItemStack toItemStack() {
        return book;
    }

}
