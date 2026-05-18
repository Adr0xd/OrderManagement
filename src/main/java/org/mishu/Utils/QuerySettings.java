package org.mishu.Utils;

/**
 * object containing the settings for q query
 *
 * @param all
 *            if true -> get all the objects for that query, false -> get the
 *            first
 * @param useLike
 *            if true, get also partial answers (substrings)
 */
public record QuerySettings(boolean all, boolean useLike) {
}