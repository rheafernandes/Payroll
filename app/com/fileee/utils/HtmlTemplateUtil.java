package com.fileee.utils;

public class HtmlTemplateUtil {

    public static final String template =
            "<table style=\"width:100%\">\n" +
                    "    <caption>Employee Details</caption>\n" +
                    "    <tr>\n" +
                    "        <th>Name</th>\n" +
                    "        <th>Pay Type</th>\n" +
                    "        <th>From</th>\n" +
                    "        <th>To</th>\n" +
                    "        <th>Salary</th>\n" +
                    "    </tr>\n" +
                    "    <tr>\n" +
                    "        <td>${name}</td>\n" +
                    "        <td>${payType}</td>\n" +
                    "        <td>${from}</td>\n" +
                    "        <td>${to}</td>\n" +
                    "        <td>${salary}</td>\n" +
                    "    </tr>\n" +
                    "</table>";
}
