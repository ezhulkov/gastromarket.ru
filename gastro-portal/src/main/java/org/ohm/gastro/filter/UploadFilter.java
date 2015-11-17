package org.ohm.gastro.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
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
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by ezhulkov on 08.01.15.
 */
public class UploadFilter extends BaseApplicationFilter implements Logging {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        try {

            final ImageService imageService = ApplicationContextHolder.getApplicationContext().getBean(ImageService.class);
            final List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(httpServletRequest);
            final byte[] imageBuf = items.stream().filter(t -> "qqfile".equals(t.getFieldName())).map(FileItem::get).findFirst().orElseThrow(() -> new RuntimeException("no qqfile parameter"));
            final String fileTypeStr = httpServletRequest.getParameter("file_type");
            final String objectIdStr = httpServletRequest.getParameter("object_id");
            final String targetContext = httpServletRequest.getParameter("target_context");

            checkNotNull(fileTypeStr, "file_type should not be empty");
            checkNotNull(imageBuf, "qqfile should not be empty");

            final FileType fileType = FileType.valueOf(fileTypeStr);
            final String objectId = fileType == FileType.TENDER ?
                    objectIdStr :
                    imageService.explicitlyGetObjectId(fileType, objectIdStr, targetContext, BaseComponent.getAuthenticatedUser(null).orElse(null));

            checkNotNull(objectId, "objectId should not be empty");

            final Map<ImageSize, String> imageUrls = imageService.resizeImagePack(imageBuf, fileType, objectId);

            if (fileType == FileType.TENDER) httpServletRequest.getSession().setAttribute(FileType.TENDER + "_" + objectId, imageUrls);

            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setHeader("Cache-Control", "no-cache");
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(imageUrls));
            httpServletResponse.getWriter().flush();

        } catch (Exception e) {

            Logging.logger.error("", e);
            httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        }

    }

}
