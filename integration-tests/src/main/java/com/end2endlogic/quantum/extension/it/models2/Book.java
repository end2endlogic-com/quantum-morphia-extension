package com.end2endlogic.quantum.extension.it.models2;

import java.time.LocalDate;
import java.util.Objects;

import com.end2endlogic.quantum.extension.it.models.Author;
import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.Reference;
import dev.morphia.annotations.Validation;
import dev.morphia.utils.IndexType;

@Entity(value = "books")
@Validation("{ title: { $exists:  true } }")
@Indexes(value = @Index(fields = @Field(value = "title", type = IndexType.TEXT)))
public class Book {
    @Reference(idOnly = true)
    public Author author;

    @Id
    public ObjectId id;

    @Indexed
    public LocalDate published;

    public String title;

    public Book() {
    }

    public Book(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public LocalDate getPublished() {
        return published;
    }

    public void setPublished(LocalDate published) {
        this.published = published;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, published, author);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        Book book = (Book) o;
        return Objects.equals(id, book.id) && Objects.equals(title, book.title) &&
                Objects.equals(published, book.published) && Objects.equals(author, book.author);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", published=" + published +
                ", author=" + author +
                '}';
    }
}
