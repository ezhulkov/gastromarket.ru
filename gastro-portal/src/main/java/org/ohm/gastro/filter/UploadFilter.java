package org.ohm.gastro.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.ohm.gastro.gui.mixins.BaseComponent;
import org.ohm.gastro.service.ImageService;
import org.ohm.gastro.service.ImageService.FileType;
import org.ohm.gastro.service.ImageService.ImageSize;
import org.ohm.gastro.service.impl.ApplicationContextHolder;
import org.ohm.gastro.trait.Logging;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * todo add authorization
 * Created by ezhulkov on 08.01.15.
 */
public class UploadFilter extends BaseApplicationFilter implements Logging {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        File file = null;
        try {

            final List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(httpServletRequest);
            final Map<String, String> multipartParams = items.stream().filter(FileItem::isFormField).collect(Collectors.toMap(FileItem::getFieldName,
                                                                                                                              FileItem::getString));
            final String filePath = multipartParams.get("file_path");
            final String fileTypeStr = multipartParams.get("file_type");
            final String objectIdStr = multipartParams.get("object_id");

            checkNotNull(filePath, "file_path should not be empty");
            checkNotNull(fileTypeStr, "file_type should not be empty");

            file = new File(filePath);

            final FileType fileType = FileType.valueOf(fileTypeStr);
            final String objectId = fileType == FileType.AVATAR ? BaseComponent.getAuthenticatedUser(null).map(t -> t.getId().toString()).orElse("0") : objectIdStr;

            checkNotNull(objectId, "objectId should not be empty");
            checkArgument(file.exists(), "file %s should exist", filePath);

            final Map<ImageSize, String> imageUrls = ApplicationContextHolder.getApplicationContext().getBean(ImageService.class).resizeImagePack(file, fileType, objectId);

            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setHeader("Cache-Control", "no-cache");
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(imageUrls));
            httpServletResponse.getWriter().flush();

        } catch (Exception e) {

            Logging.logger.error("", e);
            httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } finally {

            FileUtils.deleteQuietly(file);

        }

    }

}
